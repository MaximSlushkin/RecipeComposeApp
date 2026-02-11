package com.yourcompany.recipecomposeapp.features.categories.presentation.model

import androidx.compose.runtime.Immutable
import com.yourcompany.recipecomposeapp.core.utils.Constants
import com.yourcompany.recipecomposeapp.data.model.CategoryDto

@Immutable
data class CategoryUiModel(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
)

fun CategoryDto.toUiModel() = CategoryUiModel(
    id = id,
    title = title,
    description = description,
    imageUrl = Constants.getFullImageUrl(imageUrl)
)