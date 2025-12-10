package com.yourcompany.recipecomposeapp

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.yourcompany.recipecomposeapp.categories.data.RecipesRepositoryStub
import com.yourcompany.recipecomposeapp.recipedetails.RecipeDetailsScreen
import com.yourcompany.recipecomposeapp.recipes.presentation.model.toUiModel
import com.yourcompany.recipecomposeapp.ui.theme.RecipesAppTheme
import com.yourcompany.recipecomposeapp.utils.Constants
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
                            val safeCategoryId = categoryId.takeIf { it >= 0 } ?: Constants.DEFAULT_CATEGORY_ID
                            val safeCategoryTitle = categoryTitle.ifEmpty { Constants.DEFAULT_CATEGORY_TITLE }
                            val safeImageUrl = imageUrl.ifEmpty { Constants.DEFAULT_CATEGORY_IMAGE_URL }

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
                    FavoritesScreen(
                        modifier = Modifier,
                        onRecipeClick = { recipeId, recipe ->
                            navController.navigate(Destination.RecipeDetail.createRoute(recipeId))
                        }
                    )
                }

                composable(
                    route = Destination.Recipes.route,
                    arguments = Destination.Recipes.arguments
                ) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getInt(Constants.KEY_CATEGORY_ID) ?: Constants.DEFAULT_CATEGORY_ID
                    val categoryTitle = backStackEntry.arguments?.getString(Constants.KEY_CATEGORY_TITLE) ?: Constants.DEFAULT_CATEGORY_TITLE
                    val categoryImageUrl = backStackEntry.arguments?.getString(Constants.KEY_CATEGORY_IMAGE_URL) ?: Constants.DEFAULT_CATEGORY_IMAGE_URL

                    println("Навигация: categoryId=$categoryId, title='$categoryTitle', image='$categoryImageUrl'")

                    RecipesScreen(
                        modifier = Modifier,
                        onRecipeClick = { recipeId, recipe ->
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
                    path.removePrefix("recipe/").toIntOrNull() ?: -1
                } else {
                    null
                }
            }

            uriString.startsWith("https://recipes.androidsprint.ru/recipe/") -> {
                uriString.removePrefix("https://recipes.androidsprint.ru/recipe/").toIntOrNull() ?: -1
            }

            else -> null
        }
    } catch (e: Exception) {
        println("Ошибка парсинга URI: ${e.message}")
        null
    }
}

private fun getRecipeById(recipeId: Int) = RecipesRepositoryStub
    .getCategories()
    .flatMap { category ->
        RecipesRepositoryStub.getRecipesByCategoryId(category.id)
    }
    .find { it.id == recipeId }