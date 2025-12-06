package com.yourcompany.recipecomposeapp.utils

object Constants {
    const val ASSETS_URI_PREFIX = "file:///android_asset/"

    const val DEEP_LINK_SCHEME = "recipeapp"
    const val DEEP_LINK_BASE_URL = "https://recipes.androidsprint.ru"
    const val PARAM_RECIPE_ID = "recipeId"

    const val KEY_CATEGORY_ID = "categoryId"
    const val KEY_CATEGORY_TITLE = "categoryTitle"
    const val KEY_CATEGORY_IMAGE_URL = "categoryImageUrl"

    const val DEFAULT_CATEGORY_ID = -1
    const val DEFAULT_CATEGORY_TITLE = "Рецепты"
    const val DEFAULT_CATEGORY_IMAGE_URL = ""

    fun createRecipeDeepLink(recipeId: Int): String {
        return "$DEEP_LINK_BASE_URL/recipe/$recipeId"
    }
}