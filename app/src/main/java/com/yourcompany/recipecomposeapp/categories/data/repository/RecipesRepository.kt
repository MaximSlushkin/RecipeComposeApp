package com.yourcompany.recipecomposeapp.categories.data.repository

import com.yourcompany.recipecomposeapp.data.model.CategoryDto
import com.yourcompany.recipecomposeapp.data.model.RecipeDto
import kotlinx.coroutines.flow.Flow

interface RecipesRepository {

    fun getCategories(): Flow<List<CategoryDto>>

    fun getRecipesByCategory(categoryId: Int): Flow<List<RecipeDto>>

    suspend fun getRecipe(recipeId: Int): RecipeDto?

    suspend fun refreshCategories()

    suspend fun refreshRecipes(categoryId: Int)

    suspend fun clearCache()

    suspend fun getCategoriesLegacy(): List<CategoryDto>

    suspend fun getRecipesByCategoryLegacy(categoryId: Int): List<RecipeDto>

    suspend fun invalidateCache(categoryId: Int)
}