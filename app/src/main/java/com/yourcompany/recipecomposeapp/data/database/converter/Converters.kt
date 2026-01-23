package com.yourcompany.recipecomposeapp.data.database.converter

import androidx.room.TypeConverter
import com.yourcompany.recipecomposeapp.data.model.IngredientDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return try {
            Json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromIngredientList(value: List<IngredientDto>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toIngredientList(value: String): List<IngredientDto> {
        return try {
            Json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromIntList(value: List<Int>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toIntList(value: String): List<Int> {
        return try {
            Json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
}