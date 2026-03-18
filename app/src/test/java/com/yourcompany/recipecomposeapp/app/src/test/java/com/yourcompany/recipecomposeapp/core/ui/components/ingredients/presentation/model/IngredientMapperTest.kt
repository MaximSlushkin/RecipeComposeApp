package com.yourcompany.recipecomposeapp.app.src.test.java.com.yourcompany.recipecomposeapp.core.ui.components.ingredients.presentation.model

import com.yourcompany.recipecomposeapp.core.ui.components.ingredients.presentation.model.toUiModel
import com.yourcompany.recipecomposeapp.data.model.IngredientDto
import org.junit.Assert.assertEquals
import org.junit.Test

class IngredientMapperTest {

    @Test
    fun `toUiModel converts IngredientDto to IngredientUiModel correctly`() {
        val dto = IngredientDto(
            quantity = "500",
            unitOfMeasure = "г",
            description = "Говяжий фарш"
        )

        val result = dto.toUiModel()

        assertEquals("Говяжий фарш", result.name)
        assertEquals("500 г", result.amount)
    }

    @Test
    fun `toUiModel handles empty unit of measure correctly`() {
        val dto = IngredientDto(
            quantity = "2",
            unitOfMeasure = "",
            description = "Яйца"
        )

        val result = dto.toUiModel()

        assertEquals("Яйца", result.name)
        assertEquals("2", result.amount)
    }

    @Test
    fun `toUiModel handles empty quantity correctly`() {
        val dto = IngredientDto(
            quantity = "",
            unitOfMeasure = "шт",
            description = "Помидоры"
        )

        val result = dto.toUiModel()

        assertEquals("Помидоры", result.name)
        assertEquals("шт", result.amount)
    }

    @Test
    fun `toUiModel handles both quantity and unit empty correctly`() {
        val dto = IngredientDto(
            quantity = "",
            unitOfMeasure = "",
            description = "Соль по вкусу"
        )

        val result = dto.toUiModel()

        assertEquals("Соль по вкусу", result.name)
        assertEquals("", result.amount)
    }

    @Test
    fun `toUiModel handles decimal quantity correctly`() {
        val dto = IngredientDto(
            quantity = "0.5",
            unitOfMeasure = "ч.л.",
            description = "Соль"
        )

        val result = dto.toUiModel()

        assertEquals("Соль", result.name)
        assertEquals("0.5 ч.л.", result.amount)
    }
}