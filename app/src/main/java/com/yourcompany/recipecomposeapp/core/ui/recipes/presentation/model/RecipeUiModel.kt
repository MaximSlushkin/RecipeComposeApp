package com.yourcompany.recipecomposeapp.core.ui.recipes.presentation.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.yourcompany.recipecomposeapp.utils.Constants
import com.yourcompany.recipecomposeapp.core.ui.ingredients.presentation.model.IngredientUiModel
import com.yourcompany.recipecomposeapp.data.model.RecipeDto
import com.yourcompany.recipecomposeapp.core.ui.ingredients.presentation.model.toUiModel
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class RecipeUiModel(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val ingredients: List<IngredientUiModel>,
    val method: List<String>,
    val servings: Int = 1,
    val isFavorite: Boolean = false,
) : Parcelable

fun RecipeDto.toUiModel() = RecipeUiModel(
    id = id,
    title = title,
    imageUrl = if (imageUrl.startsWith("http")) imageUrl else Constants.ASSETS_URI_PREFIX + imageUrl,
    ingredients = ingredients.map { it.toUiModel() },
    method = method,
    servings = servings,
    isFavorite = false,
)