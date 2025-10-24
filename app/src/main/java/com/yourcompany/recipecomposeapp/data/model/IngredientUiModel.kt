package com.yourcompany.recipecomposeapp.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class IngredientUiModel(
    val name: String,
    val amount: String,
)

fun IngredientDto.toUiModel() = IngredientUiModel(
    name = description,
    amount = "$quantity $unitOfMeasure".trim()
)