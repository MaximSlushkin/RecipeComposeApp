package com.yourcompany.recipecomposeapp.data.model

import com.yourcompany.recipecomposeapp.data.database.converter.Converters
import com.yourcompany.recipecomposeapp.data.database.entity.RecipeEntity
import kotlinx.serialization.Serializable

@Serializable
data class RecipeDto(
    val id: Int,
    val title: String,
    val ingredients: List<IngredientDto>,
    val method: List<String>,
    val imageUrl: String,
    val categoryIds: List<Int> = emptyList(),
    val servings: Int = 1,
)

fun RecipeDto.toEntity(categoryId: Int): RecipeEntity {
    return RecipeEntity(
        id = this.id,
        title = this.title,
        categoryId = categoryId,
        imageUrl = this.imageUrl,
        ingredientsJson = Converters().fromIngredientList(this.ingredients),
        methodSteps = Converters().fromStringList(this.method),
        servings = this.servings
    )
}

fun RecipeEntity.toDto(): RecipeDto {
    val converters = Converters()
    return RecipeDto(
        id = this.id,
        title = this.title,
        ingredients = converters.toIngredientList(this.ingredientsJson),
        method = converters.toStringList(this.methodSteps),
        imageUrl = this.imageUrl,
        categoryIds = listOf(this.categoryId),
        servings = this.servings
    )
}