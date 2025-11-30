package com.yourcompany.recipecomposeapp

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDeepLink
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.yourcompany.recipecomposeapp.core.ui.categories.CategoriesScreen
import com.yourcompany.recipecomposeapp.core.ui.favorites.FavoritesScreen
import com.yourcompany.recipecomposeapp.core.ui.navigation.BottomNavigation
import com.yourcompany.recipecomposeapp.core.ui.navigation.Destination
import com.yourcompany.recipecomposeapp.core.ui.recipes.RecipesScreen
import com.yourcompany.recipecomposeapp.data.model.toUiModel
import com.yourcompany.recipecomposeapp.data.repository.RecipesRepositoryStub
import com.yourcompany.recipecomposeapp.features.details.ui.RecipeDetailsScreen
import com.yourcompany.recipecomposeapp.ui.theme.RecipesAppTheme
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
                    val categories = remember {
                        RecipesRepositoryStub
                            .getCategories()
                            .map { it.toUiModel() }
                    }

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
                    arguments = Destination.Recipes.arguments
                ) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: -1
                    val categories = remember { RecipesRepositoryStub.getCategories() }
                    val categoryTitle = categories.find { it.id == categoryId }?.title ?: "Рецепты"

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

                    val recipe = remember(recipeId) {
                        getRecipeById(recipeId)?.toUiModel()
                    }

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