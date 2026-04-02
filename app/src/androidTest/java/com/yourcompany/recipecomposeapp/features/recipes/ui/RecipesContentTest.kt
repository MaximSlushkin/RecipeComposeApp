package com.yourcompany.recipecomposeapp.features.recipes.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.yourcompany.recipecomposeapp.core.ui.components.ingredients.presentation.model.IngredientUiModel
import com.yourcompany.recipecomposeapp.features.recipes.presentation.model.RecipesUiState
import com.yourcompany.recipecomposeapp.features.recipes.presentation.model.RecipeUiModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class RecipesContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsLoadingState() {
        val uiState = RecipesUiState(isLoading = true)

        composeTestRule.setContent {
            RecipesContent(
                uiState = uiState,
                onRecipeClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("loading_indicator")
            .assertIsDisplayed()
    }

    @Test
    fun showsErrorState() {
        val errorMessage = "Ошибка загрузки рецептов"
        val uiState = RecipesUiState(
            errorMessage = errorMessage,
            isLoading = false
        )

        var retryCalled = false
        val onRetry = { retryCalled = true }

        composeTestRule.setContent {
            RecipesContent(
                uiState = uiState,
                onRecipeClick = {},
                onRetry = onRetry
            )
        }

        composeTestRule
            .onNodeWithTag("error_message")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(errorMessage)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Повторить")
            .performClick()

        assert(retryCalled)
    }

    @Test
    fun showsEmptyState() {
        val uiState = RecipesUiState()

        composeTestRule.setContent {
            RecipesContent(
                uiState = uiState,
                onRecipeClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("empty_state")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Рецепты для этой категории скоро появятся")
            .assertIsDisplayed()
    }

    @Test
    fun displaysRecipeList() {
        val recipes = listOf(
            RecipeUiModel(
                id = 1,
                title = "Классический бургер",
                imageUrl = "burger.jpg",
                ingredients = listOf(
                    IngredientUiModel("Говяжий фарш", "500 г"),
                    IngredientUiModel("Булочка", "2 шт")
                ),
                method = listOf("1. Приготовить котлеты", "2. Собрать бургер"),
                servings = 4
            ),
            RecipeUiModel(
                id = 2,
                title = "Цезарь с курицей",
                imageUrl = "caesar.jpg",
                ingredients = listOf(
                    IngredientUiModel("Куриное филе", "300 г"),
                    IngredientUiModel("Салат Романо", "1 шт")
                ),
                method = listOf("1. Приготовить соус", "2. Смешать ингредиенты"),
                servings = 2
            )
        )

        val uiState = RecipesUiState(
            recipes = recipes,
            isLoading = false,
            errorMessage = null
        )

        var clickedRecipeId: Int? = null

        composeTestRule.setContent {
            RecipesContent(
                uiState = uiState,
                onRecipeClick = { recipeId -> clickedRecipeId = recipeId }
            )
        }

        composeTestRule
            .onNodeWithText("КЛАССИЧЕСКИЙ БУРГЕР")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("ЦЕЗАРЬ С КУРИЦЕЙ")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("КЛАССИЧЕСКИЙ БУРГЕР")
            .performClick()

        assertEquals(1, clickedRecipeId)
    }

    @Test
    fun showsCategoryTitleFromUiState() {
        val categoryTitle = "Итальянская кухня"
        val uiState = RecipesUiState(
            categoryTitle = categoryTitle,
            recipes = emptyList(),
            isLoading = false
        )

        composeTestRule.setContent {
            RecipesContent(
                uiState = uiState,
                onRecipeClick = {}
            )
        }

        composeTestRule
            .onNodeWithText(categoryTitle.uppercase())
            .assertIsDisplayed()
    }

    @Test
    fun handlesEmptyRecipeListGracefully() {
        val uiState = RecipesUiState(
            recipes = emptyList(),
            isLoading = false,
            errorMessage = null
        )

        composeTestRule.setContent {
            RecipesContent(
                uiState = uiState,
                onRecipeClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("empty_state")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("error_message")
            .assertDoesNotExist()
    }
}