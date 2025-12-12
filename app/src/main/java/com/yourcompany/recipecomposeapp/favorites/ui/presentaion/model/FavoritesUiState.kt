package com.yourcompany.recipecomposeapp.favorites.ui.presentaion.model

import androidx.compose.runtime.Immutable
import com.yourcompany.recipecomposeapp.recipes.presentation.model.RecipeUiModel

@Immutable
data class FavoritesUiState(
    val favoriteRecipes: List<RecipeUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEmpty: Boolean = false
) {
    val hasError: Boolean
        get() = errorMessage != null
}