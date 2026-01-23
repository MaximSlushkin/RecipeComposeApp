package com.yourcompany.recipecomposeapp.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipes",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["category_id"]),
        Index(value = ["title"])
    ]
)
data class RecipeEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "category_id")
    val categoryId: Int,

    @ColumnInfo(name = "image_url")
    val imageUrl: String,

    @ColumnInfo(name = "ingredients_json")
    val ingredientsJson: String,

    @ColumnInfo(name = "method_steps")
    val methodSteps: String,

    @ColumnInfo(name = "servings")
    val servings: Int = 1
)