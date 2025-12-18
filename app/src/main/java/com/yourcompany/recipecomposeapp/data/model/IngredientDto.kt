package com.yourcompany.recipecomposeapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class IngredientDto(
    val quantity: String,
    val unitOfMeasure: String,
    val description: String,
)