package com.yourcompany.recipecomposeapp.core.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.yourcompany.recipecomposeapp.utils.Constants
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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
        route = "recipes/{${Constants.KEY_CATEGORY_ID}}/{${Constants.KEY_CATEGORY_TITLE}}/{${Constants.KEY_CATEGORY_IMAGE_URL}}",
        title = "Рецепты"
    ) {
        fun createRoute(categoryId: Int, categoryTitle: String, categoryImageUrl: String): String {
            val encodedTitle = encodeParameter(categoryTitle)
            val encodedImageUrl = encodeParameter(categoryImageUrl)
            return "recipes/$categoryId/$encodedTitle/$encodedImageUrl"
        }

        private fun encodeParameter(parameter: String): String {
            return if (parameter.isNotEmpty()) {
                URLEncoder.encode(parameter, StandardCharsets.UTF_8.toString())
            } else {
                ""
            }
        }

        val arguments = listOf(
            navArgument(Constants.KEY_CATEGORY_ID) {
                type = NavType.IntType
                defaultValue = -1
            },
            navArgument(Constants.KEY_CATEGORY_TITLE) {
                type = NavType.StringType
                defaultValue = "Рецепты"
                nullable = true
            },
            navArgument(Constants.KEY_CATEGORY_IMAGE_URL) {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
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