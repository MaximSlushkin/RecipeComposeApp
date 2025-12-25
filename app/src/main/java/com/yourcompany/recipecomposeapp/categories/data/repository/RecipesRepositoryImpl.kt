package com.yourcompany.recipecomposeapp.categories.data.repository

import android.util.Log
import com.yourcompany.recipecomposeapp.core.network.api.RecipesApiService
import com.yourcompany.recipecomposeapp.data.model.CategoryDto
import com.yourcompany.recipecomposeapp.data.model.RecipeDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipesRepositoryImpl(
    private val apiService: RecipesApiService
) : RecipesRepository {

    private companion object {
        const val TAG = "RecipesRepository"
    }

    override suspend fun getCategories(): List<CategoryDto> {
        return try {

            withContext(Dispatchers.IO) {
                apiService.getCategories().also { categories ->
                    Log.d(TAG, "Получено ${categories.size} категорий из сети")
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
                apiService.getRecipesByCategory(categoryId).also { recipes ->
                    Log.d(TAG, "Получено ${recipes.size} рецептов для категории $categoryId")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при загрузке рецептов категории $categoryId: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getRecipe(recipeId: Int): RecipeDto? {
        return try {
            withContext(Dispatchers.IO) {

                val categories = apiService.getCategories()
                categories.forEach { category ->
                    val recipes = apiService.getRecipesByCategory(category.id)
                    recipes.find { it.id == recipeId }?.let { return@withContext it }
                }
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при загрузке рецепта $recipeId: ${e.message}", e)
            null
        }
    }
}