package com.yourcompany.recipecomposeapp.recipedetails.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yourcompany.recipecomposeapp.categories.data.RecipesRepositoryStub
import com.yourcompany.recipecomposeapp.recipes.presentation.model.RecipeUiModel
import com.yourcompany.recipecomposeapp.recipedetails.presentation.model.RecipeDetailsUiState
import com.yourcompany.recipecomposeapp.recipes.presentation.model.toUiModel
import com.yourcompany.recipecomposeapp.utils.FavoriteDataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeDetailsViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val favoriteManager = FavoriteDataStoreManager(application)
    private val repository = RecipesRepositoryStub

    private val _uiState = MutableStateFlow(RecipeDetailsUiState())
    val uiState: StateFlow<RecipeDetailsUiState> = _uiState.asStateFlow()

    private val recipeId: Int = getRecipeIdFromSavedState()

    init {
        if (recipeId != -1) {
            loadRecipe()
            setupReactiveSubscriptions()
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    errorMessage = "Неверный ID рецепта",
                    isLoading = false
                )
            }
        }
    }

    private fun getRecipeIdFromSavedState(): Int {
        return savedStateHandle.get<Int>("recipeId")
            ?: savedStateHandle.get<String>("recipeId")?.toIntOrNull()
            ?: -1
    }

    fun loadRecipe() {
        if (_uiState.value.recipe != null && _uiState.value.recipe?.id == recipeId) {
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val recipeDto = getRecipeFromRepository(recipeId)

                if (recipeDto != null) {
                    val recipe = recipeDto.toUiModel()
                    _uiState.update { currentState ->
                        currentState.copy(
                            recipe = recipe,
                            currentPortions = recipe.servings,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = "Рецепт не найден"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = "Ошибка загрузки: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    private suspend fun getRecipeFromRepository(recipeId: Int) = repository
        .getCategories()
        .flatMap { category -> repository.getRecipesByCategoryId(category.id) }
        .find { it.id == recipeId }

    private fun setupReactiveSubscriptions() {
        viewModelScope.launch {
            combine(
                favoriteManager.isFavoriteFlow(recipeId),
                _uiState
            ) { isFavorite, currentState ->
                currentState.copy(isFavorite = isFavorite)
            }.collect { newState ->
                _uiState.update { newState }
            }
        }
    }

    fun initializeWithRecipe(recipe: RecipeUiModel) {
        if (recipe.id != recipeId) {
            return
        }

        _uiState.update { currentState ->
            currentState.copy(
                recipe = recipe,
                currentPortions = recipe.servings,
                isLoading = false
            )
        }

        setupReactiveSubscriptions()
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _uiState.update { it.copy(isFavoriteOperationInProgress = true) }

            try {
                favoriteManager.toggleFavorite(recipeId)
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        errorMessage = "Не удалось обновить избранное: ${e.message}",
                        isFavoriteOperationInProgress = false
                    )
                }
            }
        }
    }

    fun updatePortions(newPortions: Int) {
        if (newPortions > 0) {
            _uiState.update { currentState ->
                currentState.copy(currentPortions = newPortions)
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    class Factory(
        private val application: Application,
        private val recipeId: Int
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecipeDetailsViewModel::class.java)) {
                val savedStateHandle = SavedStateHandle().apply {
                    set("recipeId", recipeId)
                }
                return RecipeDetailsViewModel(
                    application = application,
                    savedStateHandle = savedStateHandle
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}