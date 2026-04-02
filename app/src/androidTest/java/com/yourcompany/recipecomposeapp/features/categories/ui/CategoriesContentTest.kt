package com.yourcompany.recipecomposeapp.features.categories.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.yourcompany.recipecomposeapp.features.categories.presentation.model.CategoriesUiState
import com.yourcompany.recipecomposeapp.features.categories.presentation.model.CategoryUiModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CategoriesContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysCategories() {
        val categories = listOf(
            CategoryUiModel(
                id = 1,
                title = "Завтраки",
                description = "Вкусные завтраки",
                imageUrl = "breakfast.jpg"
            ),
            CategoryUiModel(
                id = 2,
                title = "Обеды",
                description = "Сытные обеды",
                imageUrl = "lunch.jpg"
            )
        )
        val uiState = CategoriesUiState(categories = categories)

        composeTestRule.setContent {
            CategoriesContent(
                uiState = uiState,
                onCategoryClick = { _, _, _ -> }
            )
        }

        composeTestRule.onNodeWithText("ЗАВТРАКИ").assertIsDisplayed()
        composeTestRule.onNodeWithText("ОБЕДЫ").assertIsDisplayed()
    }

    @Test
    fun clickingCategoryNavigatesToRecipes() {
        var clickedId: Int? = null
        var clickedTitle: String? = null
        var clickedImageUrl: String? = null

        val categories = listOf(
            CategoryUiModel(
                id = 42,
                title = "Десерты",
                description = "Сладкие десерты",
                imageUrl = "desserts.jpg"
            )
        )
        val uiState = CategoriesUiState(categories = categories)

        composeTestRule.setContent {
            CategoriesContent(
                uiState = uiState,
                onCategoryClick = { id, title, imageUrl ->
                    clickedId = id
                    clickedTitle = title
                    clickedImageUrl = imageUrl
                }
            )
        }

        composeTestRule.onNodeWithText("ДЕСЕРТЫ").performClick()

        assertEquals(42, clickedId)
        assertEquals("Десерты", clickedTitle)
        assertEquals("desserts.jpg", clickedImageUrl)
    }

    @Test
    fun showsLoadingState() {
        val uiState = CategoriesUiState(isLoading = true)

        composeTestRule.setContent {
            CategoriesContent(
                uiState = uiState,
                onCategoryClick = { _, _, _ -> }
            )
        }

        composeTestRule
            .onNodeWithTag("loading_indicator")
            .assertIsDisplayed()
    }

    @Test
    fun showsErrorState() {
        val errorMessage = "Не удалось загрузить категории"
        val uiState = CategoriesUiState(
            error = errorMessage,
            isEmpty = true
        )

        var retryCalled = false
        val onRetry = { retryCalled = true }

        composeTestRule.setContent {
            CategoriesContent(
                uiState = uiState,
                onCategoryClick = { _, _, _ -> },
                onRetry = onRetry
            )
        }

        composeTestRule
            .onNodeWithTag("error_message")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Повторить")
            .performClick()

        assert(retryCalled)
    }

    @Test
    fun showsEmptyState() {
        val uiState = CategoriesUiState(isEmpty = true)

        composeTestRule.setContent {
            CategoriesContent(
                uiState = uiState,
                onCategoryClick = { _, _, _ -> }
            )
        }

        composeTestRule
            .onNodeWithTag("empty_state")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Категории не найдены")
            .assertIsDisplayed()
    }
}