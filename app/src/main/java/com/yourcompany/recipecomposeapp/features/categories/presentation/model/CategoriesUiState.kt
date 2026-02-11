package com.yourcompany.recipecomposeapp.features.categories.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class CategoriesUiState(

    val categories: List<CategoryUiModel> = emptyList(),

    val isLoading: Boolean = false,

    val error: String? = null,

    val isEmpty: Boolean = false
)