package com.yourcompany.recipecomposeapp.categories.data.repository

import android.util.Log
import com.yourcompany.recipecomposeapp.core.network.api.RecipesApiService
import com.yourcompany.recipecomposeapp.data.model.CategoryDto
import com.yourcompany.recipecomposeapp.data.model.RecipeDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class RecipesRepositoryImpl(
    private val apiService: RecipesApiService
) : RecipesRepository {

    private companion object {
        const val TAG = "RecipesRepository"
    }

    private val recipesCache = mutableMapOf<Int, List<RecipeDto>>()
    private val cacheMutex = Mutex()

    private var categoriesCache: List<CategoryDto>? = null

    override suspend fun getCategories(): List<CategoryDto> {
        return try {
            withContext(Dispatchers.IO) {

                categoriesCache ?: apiService.getCategories().also { categories ->
                    Log.d(TAG, "Получено ${categories.size} категорий из сети")
                    categoriesCache = categories
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при загрузке категорий: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getRecipesByCategory(categoryId: Int): List<RecipeDto> {
        return try {
            withContext(Dispatchers.IO) {

                val cachedRecipes = cacheMutex.withLock { recipesCache[categoryId] }
                if (cachedRecipes != null) {
                    Log.d(TAG, "Используем кеш для категории $categoryId: ${cachedRecipes.size} рецептов")
                    return@withContext cachedRecipes
                }

                val recipes = apiService.getRecipesByCategory(categoryId).also { recipes ->
                    Log.d(TAG, "Загружено ${recipes.size} рецептов для категории $categoryId")
                }

                cacheMutex.withLock {
                    recipesCache[categoryId] = recipes
                }

                recipes
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при загрузке рецептов категории $categoryId: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getRecipe(recipeId: Int): RecipeDto? {
        return try {
            withContext(Dispatchers.IO) {

                val cachedRecipe = findRecipeInCache(recipeId)
                if (cachedRecipe != null) {
                    Log.d(TAG, "Рецепт $recipeId найден в кеше")
                    return@withContext cachedRecipe
                }

                Log.d(TAG, "Рецепт $recipeId не найден в кеше, загружаем из сети...")

                val categories = getCategories()

                for (category in categories) {
                    val recipes = getRecipesByCategory(category.id)
                    recipes.find { it.id == recipeId }?.let { return@withContext it }
                }

                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при загрузке рецепта $recipeId: ${e.message}", e)
            null
        }
    }

    private suspend fun findRecipeInCache(recipeId: Int): RecipeDto? {
        return cacheMutex.withLock {
            for ((_, recipes) in recipesCache) {
                recipes.find { it.id == recipeId }?.let { return it }
            }
            null
        }
    }

    suspend fun clearCache() {
        cacheMutex.withLock {
            recipesCache.clear()
            categoriesCache = null
            Log.d(TAG, "Кеш очищен")
        }
    }

    suspend fun invalidateCache(categoryId: Int) {
        cacheMutex.withLock {
            recipesCache.remove(categoryId)
            Log.d(TAG, "Кеш для категории $categoryId очищен")
        }
    }
}