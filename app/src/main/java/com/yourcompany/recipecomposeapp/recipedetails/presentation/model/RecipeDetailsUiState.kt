package com.yourcompany.recipecomposeapp.recipedetails.presentation.model

import androidx.compose.runtime.Immutable
import com.yourcompany.recipecomposeapp.recipes.presentation.model.RecipeUiModel

@Immutable
data class RecipeDetailsUiState(
    val recipe: RecipeUiModel? = null,
    val currentPortions: Int = 1,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isFavorite: Boolean = false,
    val isFavoriteOperationInProgress: Boolean = false
) {
    val multiplier: Float
        get() = if (recipe?.servings != null && recipe.servings > 0) {
            currentPortions.toFloat() / recipe.servings.toFloat()
        } else {
            1.0f
        }

    val isEmpty: Boolean
        get() = recipe == null && !isLoading && errorMessage == null

    val hasError: Boolean
        get() = errorMessage != null
}