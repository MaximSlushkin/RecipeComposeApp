package com.yourcompany.recipecomposeapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.yourcompany.recipecomposeapp.core.ui.categories.CategoriesScreen
import com.yourcompany.recipecomposeapp.core.ui.favorites.FavoritesScreen
import com.yourcompany.recipecomposeapp.core.ui.navigation.BottomNavigation
import com.yourcompany.recipecomposeapp.core.ui.recipes.RecipesScreen
import com.yourcompany.recipecomposeapp.data.model.toUiModel
import com.yourcompany.recipecomposeapp.data.repository.RecipesRepositoryStub
import com.yourcompany.recipecomposeapp.ui.theme.RecipesAppTheme

@Composable
fun RecipesApp() {
    RecipesAppTheme {
        var currentScreen by remember { mutableStateOf(ScreenId.CATEGORIES) }

        var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

        var selectedCategoryTitle by remember { mutableStateOf<String?>(null) }

        Scaffold(bottomBar = {
            BottomNavigation(
                onCategoriesClick = {
                    currentScreen = ScreenId.CATEGORIES
                    selectedCategoryId = null
                    selectedCategoryTitle = null
                },
                onFavoritesClick = {
                    currentScreen = ScreenId.FAVORITES
                },
            )
        }
        ) { innerPadding ->
            when (currentScreen) {
                ScreenId.CATEGORIES -> {
                    val categories = RecipesRepositoryStub
                        .getCategories()
                        .map { it.toUiModel() }
                    CategoriesScreen(
                        modifier = Modifier.padding(innerPadding),
                        categories = categories,
                        onCategoryClick = { categoryId, categoryTitle ->
                            selectedCategoryId = categoryId
                            selectedCategoryTitle = categoryTitle
                            currentScreen = ScreenId.RECIPES
                        }
                    )
                }

                ScreenId.FAVORITES -> FavoritesScreen(
                    modifier = Modifier.padding(innerPadding)
                )

                ScreenId.RECIPES -> {
                    if (selectedCategoryId != null && selectedCategoryTitle != null) {
                        RecipesScreen(
                            categoryId = selectedCategoryId!!,
                            categoryTitle = selectedCategoryTitle!!,
                            modifier = Modifier.padding(innerPadding),
                            onRecipeClick = { recipeId ->

                            }
                        )
                    } else {
                        val categories = RecipesRepositoryStub
                            .getCategories()
                            .map { it.toUiModel() }
                        CategoriesScreen(
                            modifier = Modifier.padding(innerPadding),
                            categories = categories,
                            onCategoryClick = { categoryId, categoryTitle ->
                                selectedCategoryId = categoryId
                                selectedCategoryTitle = categoryTitle
                                currentScreen = ScreenId.RECIPES
                            }
                        )
                    }
                }
            }
        }
    }
}