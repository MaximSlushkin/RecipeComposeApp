package com.yourcompany.recipecomposeapp.core.ui.favorites

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.yourcompany.recipecomposeapp.data.datastore.PreferencesKeys
import com.yourcompany.recipecomposeapp.data.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FavoriteDataStoreManager(private val context: Context) {

    /**
     * Проверяет, добавлен ли рецепт в избранное
     * Использует .first() для однократного чтения текущего состояния
     */
    suspend fun isFavorite(recipeId: Int): Boolean {
        val preferences = context.dataStore.data.first()
        val favoriteIds = preferences[PreferencesKeys.FAVORITE_RECIPE_IDS] ?: emptySet()
        return favoriteIds.contains(recipeId.toString())
    }

    /**
     * Добавляет рецепт в избранное
     * edit{} обеспечивает атомарность операции - либо все изменения применяются, либо не одной
     */
    suspend fun addFavorite(recipeId: Int) {
        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_RECIPE_IDS] ?: emptySet()
            val updatedFavorites = currentFavorites + recipeId.toString()
            preferences[PreferencesKeys.FAVORITE_RECIPE_IDS] = updatedFavorites
        }
    }

    /**
     * Удаляет рецепт из избранного
     */
    suspend fun removeFavorite(recipeId: Int) {
        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITE_RECIPE_IDS] ?: emptySet()
            val updatedFavorites = currentFavorites - recipeId.toString()
            preferences[PreferencesKeys.FAVORITE_RECIPE_IDS] = updatedFavorites
        }
    }

    /**
     * Получает Flow со всеми избранными ID
     * Полезно для реактивного обновления UI при изменении избранного
     */
    fun getAllFavoritesFlow(): Flow<Set<String>> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.FAVORITE_RECIPE_IDS] ?: emptySet()
        }
    }

    /**
     * Получает все избранные ID (однократное чтение)
     */
    suspend fun getAllFavorites(): Set<String> {
        val preferences = context.dataStore.data.first()
        return preferences[PreferencesKeys.FAVORITE_RECIPE_IDS] ?: emptySet()
    }

    /**
     * Переключает состояние избранного для рецепта
     */
    suspend fun toggleFavorite(recipeId: Int) {
        if (isFavorite(recipeId)) {
            removeFavorite(recipeId)
        } else {
            addFavorite(recipeId)
        }
    }
}