package com.yourcompany.recipecomposeapp

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.yourcompany.recipecomposeapp.core.navigation.Destination
import com.yourcompany.recipecomposeapp.core.ui.BottomNavigation
import com.yourcompany.recipecomposeapp.core.utils.Constants
import com.yourcompany.recipecomposeapp.features.categories.presentation.CategoriesViewModel
import com.yourcompany.recipecomposeapp.features.categories.ui.CategoriesScreen
import com.yourcompany.recipecomposeapp.features.favorites.presentation.FavoritesViewModel
import com.yourcompany.recipecomposeapp.features.favorites.ui.FavoritesScreen
import com.yourcompany.recipecomposeapp.features.recipes.presentation.RecipesViewModel
import com.yourcompany.recipecomposeapp.features.recipes.ui.RecipesScreen
import com.yourcompany.recipecomposeapp.features.recipedetails.presentation.RecipeDetailsViewModel
import com.yourcompany.recipecomposeapp.features.recipedetails.ui.RecipeDetailsScreen
import com.yourcompany.recipecomposeapp.ui.theme.RecipesAppTheme
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

        Scaffold(
            bottomBar = {
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
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Destination.Categories.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Destination.Categories.route) {
                    val viewModel: CategoriesViewModel = hiltViewModel()

                    CategoriesScreen(
                        viewModel = viewModel,
                        modifier = Modifier,
                        onCategoryClick = { categoryId, categoryTitle, imageUrl ->
                            val safeCategoryId = categoryId.takeIf { it >= 0 }
                                ?: Constants.DEFAULT_CATEGORY_ID
                            val safeCategoryTitle = categoryTitle.ifEmpty {
                                Constants.DEFAULT_CATEGORY_TITLE
                            }
                            val safeImageUrl = imageUrl.ifEmpty {
                                Constants.DEFAULT_CATEGORY_IMAGE_URL
                            }

                            val route = Destination.Recipes.createRoute(
                                categoryId = safeCategoryId,
                                categoryTitle = safeCategoryTitle,
                                categoryImageUrl = safeImageUrl
                            )
                            navController.navigate(route)
                        }
                    )
                }

                composable(Destination.Favorites.route) {
                    val viewModel: FavoritesViewModel = hiltViewModel()

                    FavoritesScreen(
                        viewModel = viewModel,
                        modifier = Modifier,
                        onRecipeClick = { recipeId ->
                            navController.navigate(Destination.RecipeDetail.createRoute(recipeId))
                        }
                    )
                }

                composable(
                    route = Destination.Recipes.route,
                    arguments = Destination.Recipes.arguments
                ) { backStackEntry ->
                    val viewModel: RecipesViewModel = hiltViewModel()

                    RecipesScreen(
                        viewModel = viewModel,
                        modifier = Modifier,
                        onRecipeClick = { recipeId ->
                            navController.navigate(Destination.RecipeDetail.createRoute(recipeId))
                        }
                    )
                }

                composable(
                    route = Destination.RecipeDetail.route,
                    arguments = Destination.RecipeDetail.arguments,
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
                    val viewModel: RecipeDetailsViewModel = hiltViewModel()

                    RecipeDetailsScreen(
                        viewModel = viewModel,
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
                    path.removePrefix("recipe/").toIntOrNull() ?: -1
                } else {
                    null
                }
            }

            uriString.startsWith("https://recipes.androidsprint.ru/recipe/") -> {
                uriString.removePrefix("https://recipes.androidsprint.ru/recipe/").toIntOrNull()
                    ?: -1
            }

            else -> null
        }
    } catch (e: Exception) {
        null
    }
}