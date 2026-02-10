package com.yourcompany.recipecomposeapp.categories.data.repository

import android.util.Log
import com.yourcompany.recipecomposeapp.core.network.api.RecipesApiService
import com.yourcompany.recipecomposeapp.data.database.RecipesDatabase
import com.yourcompany.recipecomposeapp.data.database.dao.CategoryDao
import com.yourcompany.recipecomposeapp.data.database.dao.RecipeDao
import com.yourcompany.recipecomposeapp.data.model.CategoryDto
import com.yourcompany.recipecomposeapp.data.model.RecipeDto
import com.yourcompany.recipecomposeapp.data.model.toDto
import com.yourcompany.recipecomposeapp.data.model.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class RecipesRepositoryImpl(
    private val apiService: RecipesApiService,
    private val database: RecipesDatabase
) : RecipesRepository {

    private companion object {
        const val TAG = "RecipesRepository"
    }

    private val categoryDao: CategoryDao by lazy { database.categoryDao() }
    private val recipeDao: RecipeDao by lazy { database.recipeDao() }

    private val recipesCache = mutableMapOf<Int, List<RecipeDto>>()
    private val cacheMutex = Mutex()
    private var categoriesCache: List<CategoryDto>? = null

    private val refreshScope = CoroutineScope(Dispatchers.IO)

    override suspend fun forceLoadRecipe(recipeId: Int): RecipeDto? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Принудительная загрузка рецепта $recipeId из API")

                val existing = recipeDao.getRecipeByIdSync(recipeId)
                if (existing != null) {
                    Log.d(TAG, "Рецепт $recipeId найден в БД, возвращаем из кэша")
                    return@withContext existing.toDto()
                }

                val categories = apiService.getCategories()
                Log.d(TAG, "Загружено ${categories.size} категорий для поиска рецепта $recipeId")

                for (category in categories) {
                    try {
                        Log.d(
                            TAG,
                            "Поиск рецепта $recipeId в категории ${category.id}: ${category.title}"
                        )
                        val recipes = apiService.getRecipesByCategory(category.id)

                        recipes.find { it.id == recipeId }?.let { foundRecipe ->
                            recipeDao.insertRecipe(foundRecipe.toEntity(category.id))
                            Log.d(
                                TAG,
                                "✅ Рецепт $recipeId загружен из категории ${category.id} и сохранен в БД"
                            )
                            return@withContext foundRecipe
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Ошибка при загрузке категории ${category.id}: ${e.message}")
                    }
                }

                Log.w(TAG, "Рецепт $recipeId не найден ни в одной категории")
                null
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Критическая ошибка при принудительной загрузке рецепта $recipeId: ${e.message}",
                    e
                )
                null
            }
        }
    }

    override fun getCategories(): Flow<List<CategoryDto>> {

        refreshScope.launch {
            try {
                Log.d(TAG, "Запуск фонового обновления категорий из API")
                val freshCategories = apiService.getCategories()

                withContext(Dispatchers.IO) {

                    val categoryEntities = freshCategories.map { it.toEntity() }
                    categoryDao.insertCategories(categoryEntities)
                    Log.d(TAG, "Обновлено ${freshCategories.size} категорий в БД")
                }
            } catch (e: Exception) {

                Log.e(TAG, "Ошибка при обновлении категорий из API: ${e.message}", e)
            }
        }

        return categoryDao.getAllCategories()
            .map { entities ->
                entities.map { it.toDto() }
            }
    }

    override fun getRecipesByCategory(categoryId: Int): Flow<List<RecipeDto>> {

        refreshScope.launch {
            try {
                Log.d(TAG, "Запуск фонового обновления рецептов для категории $categoryId")
                val freshRecipes = apiService.getRecipesByCategory(categoryId)

                withContext(Dispatchers.IO) {

                    val recipeEntities = freshRecipes.map { it.toEntity(categoryId) }
                    recipeDao.insertRecipes(recipeEntities)
                    Log.d(
                        TAG,
                        "Обновлено ${freshRecipes.size} рецептов для категории $categoryId в БД"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при обновлении рецептов категории $categoryId: ${e.message}", e)
            }
        }

        return recipeDao.getRecipesByCategory(categoryId)
            .map { entities ->
                entities.map { it.toDto() }
            }
    }

    override fun getRecipe(recipeId: Int): Flow<RecipeDto?> {
        refreshScope.launch {
            try {
                val existingEntity = recipeDao.getRecipeByIdSync(recipeId)

                if (existingEntity == null) {
                    Log.d(TAG, "Рецепт $recipeId не найден в кеше, запуск поиска по API")
                    findAndCacheRecipeFromAllCategories(recipeId)
                } else {
                    Log.d(
                        TAG,
                        "Рецепт $recipeId найден в кеше (категория ${existingEntity.categoryId})"
                    )
                }

            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при инициализации загрузки рецепта $recipeId: ${e.message}", e)
            }
        }

        return recipeDao.getRecipeById(recipeId)
            .map { entity ->
                entity?.toDto()
            }
    }

    override suspend fun getRecipeSync(recipeId: Int): RecipeDto? {
        return withContext(Dispatchers.IO) {
            val entity = recipeDao.getRecipeByIdSync(recipeId)
            entity?.toDto()
        }
    }

    override suspend fun refreshCategories() {
        try {
            Log.d(TAG, "Ручное обновление категорий из API")
            val freshCategories = apiService.getCategories()
            val categoryEntities = freshCategories.map { it.toEntity() }
            categoryDao.insertCategories(categoryEntities)
            Log.d(TAG, "Категории вручную обновлены: ${freshCategories.size} шт.")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при ручном обновлении категорий: ${e.message}", e)
            throw e
        }
    }

    override suspend fun refreshRecipes(categoryId: Int) {
        try {
            Log.d(TAG, "Ручное обновление рецептов для категории $categoryId")
            val freshRecipes = apiService.getRecipesByCategory(categoryId)
            val recipeEntities = freshRecipes.map { it.toEntity(categoryId) }
            recipeDao.insertRecipes(recipeEntities)
            Log.d(TAG, "Рецепты категории $categoryId вручную обновлены: ${freshRecipes.size} шт.")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при ручном обновлении рецептов: ${e.message}", e)
            throw e
        }
    }

    override suspend fun clearCache() {
        withContext(Dispatchers.IO) {
            categoryDao.deleteAllCategories()
            recipeDao.deleteAllRecipes()

            cacheMutex.withLock {
                recipesCache.clear()
                categoriesCache = null
            }

            Log.d(TAG, "Кеш в БД очищен")
        }
    }


    override suspend fun getCategoriesLegacy(): List<CategoryDto> {
        return try {
            withContext(Dispatchers.IO) {

                val cached = cacheMutex.withLock { categoriesCache }
                if (cached != null) {
                    Log.d(TAG, "Используем legacy кеш категорий: ${cached.size} шт.")
                    return@withContext cached
                }

                val categories = apiService.getCategories()

                cacheMutex.withLock {
                    categoriesCache = categories
                }

                Log.d(TAG, "Получено ${categories.size} категорий из сети (legacy)")
                categories
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при загрузке категорий (legacy): ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getRecipesByCategoryLegacy(categoryId: Int): List<RecipeDto> {
        return try {
            withContext(Dispatchers.IO) {
                val cachedRecipes = cacheMutex.withLock { recipesCache[categoryId] }
                if (cachedRecipes != null) {
                    Log.d(
                        TAG,
                        "Используем legacy кеш для категории $categoryId: ${cachedRecipes.size} рецептов"
                    )
                    return@withContext cachedRecipes
                }

                val recipes = apiService.getRecipesByCategory(categoryId).also { recipes ->
                    Log.d(
                        TAG,
                        "Загружено ${recipes.size} рецептов для категории $categoryId (legacy)"
                    )
                }

                cacheMutex.withLock {
                    recipesCache[categoryId] = recipes
                }

                recipes
            }
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Ошибка при загрузке рецептов категории $categoryId (legacy): ${e.message}",
                e
            )
            emptyList()
        }
    }

    override suspend fun invalidateCache(categoryId: Int) {
        cacheMutex.withLock {
            recipesCache.remove(categoryId)
            Log.d(TAG, "Legacy кеш для категории $categoryId очищен")
        }
    }

    private suspend fun findAndCacheRecipeFromAllCategories(recipeId: Int) {
        try {
            Log.d(TAG, "Поиск рецепта $recipeId по всем категориям API")

            val categories = apiService.getCategories()
            Log.d(TAG, "Загружено ${categories.size} категорий для поиска")

            categoryDao.insertCategories(categories.map { it.toEntity() })

            for (category in categories) {
                try {
                    Log.d(TAG, "Поиск в категории ${category.id}: ${category.title}")
                    val recipes = apiService.getRecipesByCategory(category.id)

                    recipes.find { it.id == recipeId }?.let { foundRecipe ->

                        recipeDao.insertRecipe(foundRecipe.toEntity(category.id))
                        recipeDao.insertRecipes(recipes.map { it.toEntity(category.id) })

                        Log.d(
                            TAG,
                            "Рецепт $recipeId найден в категории ${category.id} и сохранен в БД"
                        )
                        Log.d(
                            TAG,
                            "Сохранено ${recipes.size} рецептов категории ${category.id} в кеш"
                        )
                        return
                    }
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "Ошибка при загрузке рецептов категории ${category.id}: ${e.message}",
                        e
                    )
                }
            }

            Log.w(TAG, "Рецепт $recipeId не найден ни в одной категории")

        } catch (e: Exception) {
            Log.e(TAG, "Критическая ошибка при поиске рецепта $recipeId: ${e.message}", e)
        }
    }

    private suspend fun updateRecipeFromApiIfNeeded(recipeId: Int, categoryId: Int) {
        try {
            Log.d(TAG, "Проверка обновлений для рецепта $recipeId (категория $categoryId)")

            val recipes = apiService.getRecipesByCategory(categoryId)

            recipes.find { it.id == recipeId }?.let { foundRecipe ->
                recipeDao.insertRecipe(foundRecipe.toEntity(categoryId))
                Log.d(TAG, "✅ Рецепт $recipeId обновлен из API")
            }

            recipeDao.insertRecipes(recipes.map { it.toEntity(categoryId) })
            Log.d(TAG, "✅ Обновлено ${recipes.size} рецептов категории $categoryId")

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при обновлении рецепта $recipeId: ${e.message}", e)
        }
    }
}