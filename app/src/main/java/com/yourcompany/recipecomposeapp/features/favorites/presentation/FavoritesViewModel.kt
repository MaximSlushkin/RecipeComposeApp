package com.yourcompany.recipecomposeapp.features.favorites.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.recipecomposeapp.data.repository.RecipesRepository
import com.yourcompany.recipecomposeapp.features.favorites.presentation.model.FavoritesUiState
import com.yourcompany.recipecomposeapp.features.recipes.presentation.model.RecipeUiModel
import com.yourcompany.recipecomposeapp.features.recipes.presentation.model.toUiModel
import com.yourcompany.recipecomposeapp.core.utils.FavoriteDataStoreManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(
    application: Application,
    private val repository: RecipesRepository
) : AndroidViewModel(application) {

    private val favoriteManager = FavoriteDataStoreManager(application)

    private val recipeCache = mutableMapOf<Int, RecipeUiModel>()

    private val _uiState = MutableStateFlow(FavoritesUiState(isLoading = true))
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        setupReactiveSubscription()
    }

    private fun setupReactiveSubscription() {
        viewModelScope.launch {
            favoriteManager.getFavoriteIdsFlow()
                .onStart {
                    _uiState.update { it.copy(isLoading = true) }
                }
                .map { favoriteIds ->
                    if (favoriteIds.isEmpty()) {
                        return@map emptyList<RecipeUiModel>()
                    }

                    favoriteIds.map { recipeIdStr ->
                        async {
                            val recipeId = recipeIdStr.toIntOrNull() ?: return@async null
                            loadRecipeWithRetry(recipeId)
                        }
                    }.awaitAll().filterNotNull()
                }
                .catch { exception ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = "Ошибка загрузки избранного: ${exception.message}",
                            isEmpty = true
                        )
                    }
                }
                .collect { favoriteRecipes ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            favoriteRecipes = favoriteRecipes,
                            isLoading = false,
                            isEmpty = favoriteRecipes.isEmpty(),
                            errorMessage = null
                        )
                    }
                }
        }
    }

    private suspend fun loadRecipeWithRetry(recipeId: Int): RecipeUiModel? {
        recipeCache[recipeId]?.let {
            return it
        }

        val fromDb = repository.getRecipeSync(recipeId)?.toUiModel()
        if (fromDb != null) {
            recipeCache[recipeId] = fromDb
            return fromDb
        }

        val fromApi = repository.forceLoadRecipe(recipeId)?.toUiModel()
        if (fromApi != null) {
            recipeCache[recipeId] = fromApi
            return fromApi
        }

        try {
            return repository.getRecipe(recipeId)
                .map { dto -> dto?.toUiModel() }
                .firstOrNull { it != null }
                ?.also { recipeCache[recipeId] = it }
        } catch (e: Exception) {
            return null
        }
    }

    fun refresh() {
        viewModelScope.launch {
            recipeCache.clear()
            setupReactiveSubscription()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearCache() {
        recipeCache.clear()
    }
}