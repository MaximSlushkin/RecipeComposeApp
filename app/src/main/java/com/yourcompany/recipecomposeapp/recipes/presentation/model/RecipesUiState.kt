package com.yourcompany.recipecomposeapp.recipes.presentation.model

import androidx.compose.runtime.Immutable
import com.yourcompany.recipecomposeapp.utils.Constants

@Immutable
data class RecipesUiState(
    val recipes: List<RecipeUiModel> = emptyList(),
    val categoryTitle: String = Constants.DEFAULT_CATEGORY_TITLE,
    val categoryImageUrl: String = Constants.DEFAULT_CATEGORY_IMAGE_URL,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {

    val isEmpty: Boolean
        get() = recipes.isEmpty() && !isLoading && errorMessage == null

    val hasError: Boolean
        get() = errorMessage != null

    val hasData: Boolean
        get() = recipes.isNotEmpty() && !isLoading && !hasError

    companion object {
        val Default = RecipesUiState(
            categoryTitle = Constants.DEFAULT_CATEGORY_TITLE,
            categoryImageUrl = Constants.DEFAULT_CATEGORY_IMAGE_URL
        )
    }
}