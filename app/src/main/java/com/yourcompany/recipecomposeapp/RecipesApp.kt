package com.yourcompany.recipecomposeapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourcompany.recipecomposeapp.core.ui.categories.CategoriesScreen
import com.yourcompany.recipecomposeapp.core.ui.favorites.FavoritesScreen
import com.yourcompany.recipecomposeapp.core.ui.navigation.BottomNavigation
import com.yourcompany.recipecomposeapp.core.ui.navigation.Destination
import com.yourcompany.recipecomposeapp.core.ui.recipes.RecipesScreen
import com.yourcompany.recipecomposeapp.data.model.toUiModel
import com.yourcompany.recipecomposeapp.data.repository.RecipesRepositoryStub
import com.yourcompany.recipecomposeapp.ui.theme.RecipesAppTheme

@Composable
fun RecipesApp() {
    RecipesAppTheme {
        val navController = rememberNavController()

        Scaffold(bottomBar = {
            BottomNavigation(
                navController = navController,
                onCategoriesClick = {
                    navController.navigate(Destination.Categories.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onFavoritesClick = {
                    navController.navigate(Destination.Favorites.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Destination.Categories.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Destination.Categories.route) {
                    val categories = RecipesRepositoryStub
                        .getCategories()
                        .map { it.toUiModel() }

                    CategoriesScreen(
                        modifier = Modifier,
                        categories = categories,
                        onCategoryClick = { categoryId, categoryTitle ->
                            navController.navigate(Destination.Recipes.createRoute(categoryId))
                        }
                    )
                }

                composable(Destination.Favorites.route) {
                    FavoritesScreen(
                        modifier = Modifier
                    )
                }

                composable(
                    route = Destination.Recipes.route,
                    arguments = Destination.Recipes.arguments
                ) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: -1
                    val categories = RecipesRepositoryStub.getCategories()
                    val categoryTitle = categories.find { it.id == categoryId }?.title ?: "Рецепты"

                    RecipesScreen(
                        categoryId = categoryId,
                        categoryTitle = categoryTitle,
                        modifier = Modifier,
                        onRecipeClick = { recipeId ->
                            // TODO: Будущая реализация перехода на рецепт
                        }
                    )
                }
            }
        }
    }
}