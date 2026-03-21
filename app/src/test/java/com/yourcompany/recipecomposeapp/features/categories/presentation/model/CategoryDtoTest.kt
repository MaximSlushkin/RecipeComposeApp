package com.yourcompany.recipecomposeapp.features.categories.presentation.model

import com.yourcompany.recipecomposeapp.core.utils.Constants
import com.yourcompany.recipecomposeapp.fixtures.CategoryTestFixtures
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class CategoryDtoTest {

    @Test
    fun `toUiModel adds base URL prefix to relative image path`() {
        val dto = CategoryTestFixtures.createCategoryDto(
            imageUrl = "lunch.jpg"
        )

        val result = dto.toUiModel()

        assertEquals("${Constants.IMAGES_BASE_URL}lunch.jpg", result.imageUrl)
    }

    @Test
    fun `toUiModel keeps full URL unchanged when image path starts with http`() {
        val fullUrl = "https://example.com/images/burger.jpg"
        val dto = CategoryTestFixtures.createCategoryDto(
            imageUrl = fullUrl
        )

        val result = dto.toUiModel()

        assertEquals(fullUrl, result.imageUrl)
    }

    @Test
    fun `toUiModel handles empty image URL`() {
        val dto = CategoryTestFixtures.createCategoryDto(
            imageUrl = ""
        )

        val result = dto.toUiModel()

        assertEquals(Constants.IMAGES_BASE_URL, result.imageUrl)
    }

    @Test
    fun `toUiModel handles empty title correctly`() {
        val dto = CategoryTestFixtures.createCategoryWithEmptyTitle(
            id = 5,
            description = "Category with no name"
        )

        val result = dto.toUiModel()

        assertEquals("", result.title)
        assertEquals("Category with no name", result.description)
    }

    @Test
    fun `toUiModel handles very long description correctly`() {
        val dto = CategoryTestFixtures.createCategoryWithLongDescription(
            id = 10,
            title = "Long Description Category"
        )

        val result = dto.toUiModel()

        assertEquals(dto.description, result.description)
        assertTrue(result.description.length > 1000)
    }

    @Test
    fun `toUiModel preserves all fields when mapping`() {
        val dto = CategoryTestFixtures.createCategoryDto(
            id = 7,
            title = "Breakfast",
            description = "Delicious breakfast recipes",
            imageUrl = "breakfast.jpg"
        )

        val result = dto.toUiModel()

        assertEquals(dto.id, result.id)
        assertEquals(dto.title, result.title)
        assertEquals(dto.description, result.description)
        assertEquals(Constants.IMAGES_BASE_URL + dto.imageUrl, result.imageUrl)
    }
}