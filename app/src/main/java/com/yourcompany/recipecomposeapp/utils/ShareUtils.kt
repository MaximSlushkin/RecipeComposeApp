package com.yourcompany.recipecomposeapp.utils

import android.content.Context
import android.content.Intent
import com.yourcompany.recipecomposeapp.Constants

object ShareUtils {

    fun shareRecipe(context: Context, recipeId: Int, recipeTitle: String) {
        val shareLink = Constants.createRecipeDeepLink(recipeId)
        val shareText = "Попробуй этот рецепт: $recipeTitle\n$shareLink"

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "Рецепт: $recipeTitle")
        }

        context.startActivity(
            Intent.createChooser(shareIntent, "Поделиться рецептом")
        )
    }
}