package com.yourcompany.recipecomposeapp.features.recipes.presentation.model

import com.yourcompany.recipecomposeapp.core.utils.Constants
import com.yourcompany.recipecomposeapp.fixtures.RecipeTestFixtures
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class RecipeDtoMapperTest {

    @Test
    fun `maps DTO to UI model correctly - проверка id и title`() {
        val dto = RecipeTestFixtures.createRecipeDto(
            id = 42,
            title = "Spaghetti Carbonara"
        )

        val result = dto.toUiModel()

        assertEquals(42, result.id)
        assertEquals("Spaghetti Carbonara", result.title)
    }

    @Test
    fun `prepends base url to relative imageUrl`() {
        val relativePath = "pasta.jpg"
        val dto = RecipeTestFixtures.createRecipeDto(
            imageUrl = relativePath
        )

        val result = dto.toUiModel()

        val expectedUrl = Constants.IMAGES_BASE_URL + relativePath
        assertEquals(expectedUrl, result.imageUrl)
    }

    @Test
    fun `preserves full imageUrl starting with http`() {
        val fullUrl = "https://custom-cdn.com/images/recipe123.jpg"
        val dto = RecipeTestFixtures.createRecipeDto(
            imageUrl = fullUrl
        )

        val result = dto.toUiModel()

        assertEquals(fullUrl, result.imageUrl)
    }

    @Test
    fun `maps ingredients correctly preserving structure and order`() {
        val dto = RecipeTestFixtures.createRecipeDto()

        val result = dto.toUiModel()

        assertEquals(dto.ingredients.size, result.ingredients.size)

        dto.ingredients.forEachIndexed { index, ingredientDto ->
            val mappedIngredient = result.ingredients[index]
            assertEquals(ingredientDto.description, mappedIngredient.name)
            assertEquals(
                "${ingredientDto.quantity} ${ingredientDto.unitOfMeasure}".trim(),
                mappedIngredient.amount
            )
        }
    }

    @Test
    fun `maps method steps correctly`() {
        val dto = RecipeTestFixtures.createRecipeDto()

        val result = dto.toUiModel()

        assertEquals(dto.method, result.method)
    }

    @Test
    fun `handles empty ingredients list`() {

        val dto = RecipeTestFixtures.createMinimalRecipeDto()

        val result = dto.toUiModel()

        assertTrue(result.ingredients.isEmpty())
    }

    @Test
    fun `handles empty method steps`() {
        val dto = RecipeTestFixtures.createMinimalRecipeDto()

        val result = dto.toUiModel()

        assertTrue(result.method.isEmpty())
    }

    @Test
    fun `preserves servings count`() {
        val customServings = 6
        val dto = RecipeTestFixtures.createRecipeDto(servings = customServings)

        val result = dto.toUiModel()

        assertEquals(customServings, result.servings)
    }
}