package com.yourcompany.recipecomposeapp.data.model

import com.yourcompany.recipecomposeapp.data.database.entity.CategoryEntity
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String
)

fun CategoryDto.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        imageUrl = this.imageUrl
    )
}

fun CategoryEntity.toDto(): CategoryDto {
    return CategoryDto(
        id = this.id,
        title = this.title,
        description = this.description,
        imageUrl = this.imageUrl
    )
}