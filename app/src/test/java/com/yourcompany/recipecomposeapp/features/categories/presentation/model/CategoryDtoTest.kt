package com.yourcompany.recipecomposeapp.features.categories.presentation.model

import com.yourcompany.recipecomposeapp.core.utils.Constants
import com.yourcompany.recipecomposeapp.data.model.CategoryDto
import junit.framework.TestCase.assertEquals
import org.junit.Test

class CategoryDtoTest {

    @Test
    fun `toUiModel adds base URL prefix to relative image path`() {
        val dto = CategoryDto(
            id = 1,
            title = "Бургеры",
            description = "Вкусные бургеры",
            imageUrl = "lunch.jpg"
        )

        val result = dto.toUiModel()

        assertEquals("Бургеры", result.title)
        assertEquals("Вкусные бургеры", result.description)
        assertEquals("${Constants.IMAGES_BASE_URL}lunch.jpg", result.imageUrl)
    }

    @Test
    fun `toUiModel keeps full URL unchanged when image path starts with http`() {

        val fullUrl = "https://example.com/images/burger.jpg"
        val dto = CategoryDto(
            id = 2,
            title = "Пицца",
            description = "Итальянская пицца",
            imageUrl = fullUrl
        )

        val result = dto.toUiModel()

        assertEquals("Пицца", result.title)
        assertEquals("Итальянская пицца", result.description)
        assertEquals(fullUrl, result.imageUrl)
    }

    @Test
    fun `toUiModel handles empty image URL`() {

        val dto = CategoryDto(
            id = 3,
            title = "Десерты",
            description = "Сладкие десерты",
            imageUrl = ""
        )

        val result = dto.toUiModel()

        assertEquals("Десерты", result.title)
        assertEquals("Сладкие десерты", result.description)
        assertEquals(Constants.IMAGES_BASE_URL, result.imageUrl)
    }
}