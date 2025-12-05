package com.yourcompany.recipecomposeapp

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.yourcompany.recipecomposeapp.categories.ui.CategoriesScreen
import com.yourcompany.recipecomposeapp.favorites.ui.FavoritesScreen
import com.yourcompany.recipecomposeapp.core.ui.BottomNavigation
import com.yourcompany.recipecomposeapp.core.ui.navigation.Destination
import com.yourcompany.recipecomposeapp.recipes.ui.RecipesScreen
import com.yourcompany.recipecomposeapp.categories.presentation.model.toUiModel
import com.yourcompany.recipecomposeapp.categories.data.RecipesRepositoryStub
import com.yourcompany.recipecomposeapp.recipedetails.RecipeDetailsScreen
import com.yourcompany.recipecomposeapp.recipes.presentation.model.toUiModel
import com.yourcompany.recipecomposeapp.ui.theme.RecipesAppTheme
import com.yourcompany.recipecomposeapp.utils.Constants
import com.yourcompany.recipecomposeapp.utils.FavoriteDataStoreManager
import kotlinx.coroutines.delay

@Composable
fun RecipesApp(deepLinkIntent: Intent? = null) {
    RecipesAppTheme {
        val navController = rememberNavController()
        val context = LocalContext.current

        LaunchedEffect(deepLinkIntent) {
            deepLinkIntent?.data?.let { uri ->
                val recipeId = parseRecipeIdFromUri(uri.toString())

                if (recipeId != null) {
                    delay(100)

                    navController.navigate(
                        Destination.RecipeDetail.createRoute(recipeId)
                    ) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            }
        }

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
                    CategoriesScreen(
                        modifier = Modifier,
                        onCategoryClick = { categoryId, categoryTitle, imageUrl ->
                            navController.currentBackStackEntry?.savedStateHandle?.apply {
                                set("categoryTitle", categoryTitle)
                                set("categoryImageUrl", imageUrl)
                            }
                            navController.navigate(Destination.Recipes.createRoute(categoryId))
                        }
                    )
                }

                composable(Destination.Favorites.route) {
                    FavoritesScreen(
                        favoriteManager = FavoriteDataStoreManager(LocalContext.current),
                        recipesRepository = RecipesRepositoryStub,
                        modifier = Modifier,
                        onRecipeClick = { recipeId, recipe ->
                            navController.navigate(Destination.RecipeDetail.createRoute(recipeId))
                        }
                    )
                }

                composable(
                    route = Destination.Recipes.route,
                    arguments = listOf(
                        navArgument("categoryId") {
                            type = NavType.IntType
                            defaultValue = -1
                        }
                    )
                ) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: -1

                    val categoryTitle = backStackEntry.savedStateHandle
                        .remove<String>("categoryTitle") ?: "Рецепты"
                    val categoryImageUrl = backStackEntry.savedStateHandle
                        .remove<String>("categoryImageUrl") ?: ""

                    RecipesScreen(
                        categoryId = categoryId,
                        categoryTitle = categoryTitle,
                        modifier = Modifier,
                        onRecipeClick = { recipeId, recipe ->
                            navController.navigate(Destination.RecipeDetail.createRoute(recipeId))
                        }
                    )
                }

                composable(
                    route = Destination.RecipeDetail.route,
                    arguments = listOf(
                        navArgument(Constants.PARAM_RECIPE_ID) {
                            type = NavType.IntType
                            defaultValue = -1
                        }
                    ),
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern =
                                "${Constants.DEEP_LINK_SCHEME}://recipe/{${Constants.PARAM_RECIPE_ID}}"
                        },
                        navDeepLink {
                            uriPattern =
                                "${Constants.DEEP_LINK_BASE_URL}/recipe/{${Constants.PARAM_RECIPE_ID}}"
                        }
                    )
                ) { backStackEntry ->
                    val recipeId = backStackEntry.arguments?.getInt(Constants.PARAM_RECIPE_ID) ?: -1

                    val recipe = getRecipeById(recipeId)?.toUiModel()

                    RecipeDetailsScreen(
                        recipeId = recipeId,
                        recipe = recipe,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

private fun parseRecipeIdFromUri(uriString: String): Int? {
    return try {
        when {
            uriString.startsWith("recipeapp://") -> {
                val path = uriString.removePrefix("recipeapp://")
                if (path.startsWith("recipe/")) {
                    path.removePrefix("recipe/").toIntOrNull()
                } else {
                    null
                }
            }

            uriString.startsWith("https://recipes.androidsprint.ru/recipe/") -> {
                uriString.removePrefix("https://recipes.androidsprint.ru/recipe/").toIntOrNull()
            }

            else -> null
        }
    } catch (e: Exception) {
        null
    }
}

private fun getRecipeById(recipeId: Int) = RecipesRepositoryStub
    .getCategories()
    .flatMap { category ->
        RecipesRepositoryStub.getRecipesByCategoryId(category.id)
    }
    .find { it.id == recipeId }