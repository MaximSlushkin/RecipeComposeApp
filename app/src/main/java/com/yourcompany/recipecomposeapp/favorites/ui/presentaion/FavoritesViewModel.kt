package com.yourcompany.recipecomposeapp.favorites.ui.presentaion

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.recipecomposeapp.categories.data.repository.RecipesRepository
import com.yourcompany.recipecomposeapp.favorites.ui.presentaion.model.FavoritesUiState
import com.yourcompany.recipecomposeapp.recipes.presentation.model.toUiModel
import com.yourcompany.recipecomposeapp.utils.FavoriteDataStoreManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(
    application: Application,
    private val repository: RecipesRepository
) : AndroidViewModel(application) {

    private val favoriteManager = FavoriteDataStoreManager(application)

    private val _uiState = MutableStateFlow(FavoritesUiState(isLoading = true))
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavoriteRecipes()
        setupReactiveSubscription()
    }

    private fun setupReactiveSubscription() {
        viewModelScope.launch {
            favoriteManager.getFavoriteIdsFlow()
                .map { favoriteIds ->
                    favoriteIds.map { recipeIdStr ->
                        async {
                            val recipeId = recipeIdStr.toIntOrNull()
                            recipeId?.let { id ->
                                repository.getRecipeSync(id)?.toUiModel()
                            }
                        }
                    }.awaitAll().filterNotNull()
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

    private fun loadFavoriteRecipes() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isLoading = true, errorMessage = null)
            }

            try {
                val favoriteIds = favoriteManager.getAllFavorites()

                val favoriteRecipes = favoriteIds.map { recipeIdStr ->
                    async {
                        val recipeId = recipeIdStr.toIntOrNull()
                        recipeId?.let { id ->
                            repository.getRecipeSync(id)?.toUiModel()
                        }
                    }
                }.awaitAll().filterNotNull()

                _uiState.update { currentState ->
                    currentState.copy(
                        favoriteRecipes = favoriteRecipes,
                        isLoading = false,
                        isEmpty = favoriteRecipes.isEmpty()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = "Не удалось загрузить избранные рецепты: ${e.message}",
                        isEmpty = true
                    )
                }
            }
        }
    }

    fun refresh() {
        loadFavoriteRecipes()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}