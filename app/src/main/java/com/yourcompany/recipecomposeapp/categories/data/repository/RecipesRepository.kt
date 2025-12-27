package com.yourcompany.recipecomposeapp.categories.data.repository

import com.yourcompany.recipecomposeapp.data.model.CategoryDto
import com.yourcompany.recipecomposeapp.data.model.RecipeDto

interface RecipesRepository {

    suspend fun getCategories(): List<CategoryDto>

    suspend fun getRecipesByCategory(categoryId: Int): List<RecipeDto>

    suspend fun getRecipe(recipeId: Int): RecipeDto?
}