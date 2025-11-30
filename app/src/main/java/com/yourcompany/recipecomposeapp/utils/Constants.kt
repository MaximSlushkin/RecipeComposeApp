package com.yourcompany.recipecomposeapp.utils

object Constants {
    const val ASSETS_URI_PREFIX = "file:///android_asset/"

    const val DEEP_LINK_SCHEME = "recipeapp"
    const val DEEP_LINK_BASE_URL = "https://recipes.androidsprint.ru"
    const val PARAM_RECIPE_ID = "recipeId"

    fun createRecipeDeepLink(recipeId: Int): String {
        return "$DEEP_LINK_BASE_URL/recipe/$recipeId"
    }
}