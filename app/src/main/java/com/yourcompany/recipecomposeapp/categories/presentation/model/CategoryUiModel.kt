package com.yourcompany.recipecomposeapp.categories.presentation.model

import androidx.compose.runtime.Immutable
import com.yourcompany.recipecomposeapp.utils.Constants
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
    imageUrl = if (imageUrl.startsWith("http")) imageUrl else Constants.ASSETS_URI_PREFIX + imageUrl
)