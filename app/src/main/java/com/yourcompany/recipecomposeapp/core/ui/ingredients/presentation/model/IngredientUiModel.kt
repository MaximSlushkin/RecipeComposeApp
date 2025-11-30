package com.yourcompany.recipecomposeapp.core.ui.ingredients.presentation.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.yourcompany.recipecomposeapp.data.model.IngredientDto
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class IngredientUiModel(
    val name: String,
    val amount: String,
) : Parcelable

fun IngredientDto.toUiModel() = IngredientUiModel(
    name = description,
    amount = "$quantity $unitOfMeasure".trim()
)