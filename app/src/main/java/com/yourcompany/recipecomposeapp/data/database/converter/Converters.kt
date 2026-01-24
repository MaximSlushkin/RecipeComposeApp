package com.yourcompany.recipecomposeapp.data.database.converter

import androidx.room.TypeConverter
import com.yourcompany.recipecomposeapp.data.model.IngredientDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    private companion object {
        const val STRING_LIST_DELIMITER = "|||"
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(STRING_LIST_DELIMITER)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) {
            emptyList()
        } else {
            value.split(STRING_LIST_DELIMITER)
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
        return value.joinToString(STRING_LIST_DELIMITER)
    }

    @TypeConverter
    fun toIntList(value: String): List<Int> {
        return if (value.isEmpty()) {
            emptyList()
        } else {
            try {
                value.split(STRING_LIST_DELIMITER).map { it.toInt() }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}