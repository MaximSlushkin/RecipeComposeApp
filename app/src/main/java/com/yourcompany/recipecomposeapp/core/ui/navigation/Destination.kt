package com.yourcompany.recipecomposeapp.core.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.yourcompany.recipecomposeapp.utils.Constants


sealed class Destination(
    val route: String,
    val title: String = ""
) {

    object Categories : Destination(
        route = "categories",
        title = "Категории"
    )

    object Favorites : Destination(
        route = "favorites",
        title = "Избранное"
    )

    object Recipes : Destination(
        route = "recipes/{categoryId}",
        title = "Рецепты"
    ) {
        fun createRoute(categoryId: Int): String {
            return "recipes/$categoryId"
        }

        val arguments = listOf(
            navArgument("categoryId") {
                type = NavType.IntType
                defaultValue = -1
            }
        )
    }

    object RecipeDetail : Destination(
        route = "recipe/{${Constants.PARAM_RECIPE_ID}}",
        title = "Детали рецепта"
    ) {
        fun createRoute(recipeId: Int): String {
            return "recipe/$recipeId"
        }

        val arguments = listOf(
            navArgument(Constants.PARAM_RECIPE_ID) {
                type = NavType.IntType
                defaultValue = -1
            }
        )
    }
}