package com.yourcompany.recipecomposeapp.data.model

import androidx.compose.runtime.Immutable
import com.yourcompany.recipecomposeapp.Constants

@Immutable
data class RecipeUiModel(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val ingredients: List<IngredientUiModel>,
    val method: List<String>,
    val isFavorite: Boolean = false,
)

fun RecipeDto.toUiModel() = RecipeUiModel(
    id = id,
    title = title,
    imageUrl = if (imageUrl.startsWith("http")) imageUrl else Constants.ASSETS_URI_PREFIX + imageUrl,
    ingredients = ingredients.map { it.toUiModel() },
    method = method,
    isFavorite = false,
)