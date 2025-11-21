package com.yourcompany.recipecomposeapp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class FavoritePrefsManager(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "recipe_app_prefs"
        private const val KEY_FAVORITE_RECIPE_IDS = "favorite_recipe_ids"
    }

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Проверяет, добавлен ли рецепт в избранное
     */
    fun isFavorite(recipeId: Int): Boolean {
        val favorites = sharedPreferences.getStringSet(KEY_FAVORITE_RECIPE_IDS, emptySet()) ?: emptySet()
        return favorites.contains(recipeId.toString())
    }

    /**
     * Добавляет рецепт в избранное
     */
    fun addToFavorites(recipeId: Int) {
        val currentFavorites = getFavoriteIdsSet()
        currentFavorites.add(recipeId.toString())

        sharedPreferences.edit {
            putStringSet(KEY_FAVORITE_RECIPE_IDS, currentFavorites)
        }
    }

    /**
     * Удаляет рецепт из избранного
     */
    fun removeFromFavorites(recipeId: Int) {
        val currentFavorites = getFavoriteIdsSet()
        currentFavorites.remove(recipeId.toString())

        sharedPreferences.edit {
            putStringSet(KEY_FAVORITE_RECIPE_IDS, currentFavorites)
        }
    }

    /**
     * Получает все избранные рецепты в виде Set<String>
     */
    fun getAllFavorites(): Set<String> {
        return sharedPreferences.getStringSet(KEY_FAVORITE_RECIPE_IDS, emptySet()) ?: emptySet()
    }

    /**
     * Вспомогательный метод для получения изменяемого набора ID
     */
    private fun getFavoriteIdsSet(): MutableSet<String> {
        val currentFavorites = sharedPreferences.getStringSet(KEY_FAVORITE_RECIPE_IDS, null)
        return currentFavorites?.toMutableSet() ?: mutableSetOf()
    }

    /**
     * Переключает состояние избранного для рецепта
     */
    fun toggleFavorite(recipeId: Int) {
        if (isFavorite(recipeId)) {
            removeFromFavorites(recipeId)
        } else {
            addToFavorites(recipeId)
        }
    }
}