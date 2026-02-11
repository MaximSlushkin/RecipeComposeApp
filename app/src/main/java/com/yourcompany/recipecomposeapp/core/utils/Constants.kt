package com.yourcompany.recipecomposeapp.core.utils

object Constants {

    const val IMAGES_BASE_URL = "https://recipes.androidsprint.ru/api/images/"
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

    fun getFullImageUrl(fileName: String): String {
        return if (fileName.startsWith("http")) {
            fileName
        } else {
            IMAGES_BASE_URL + fileName
        }
    }
}