package com.yourcompany.recipecomposeapp.features.recipedetails.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yourcompany.recipecomposeapp.data.repository.RecipesRepository
import com.yourcompany.recipecomposeapp.features.recipes.presentation.model.RecipeUiModel
import com.yourcompany.recipecomposeapp.features.recipedetails.presentation.model.RecipeDetailsUiState
import com.yourcompany.recipecomposeapp.features.recipes.presentation.model.toUiModel
import com.yourcompany.recipecomposeapp.core.utils.FavoriteDataStoreManager
import com.yourcompany.recipecomposeapp.core.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeDetailsViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle,
    private val repository: RecipesRepository
) : AndroidViewModel(application) {

    private val favoriteManager = FavoriteDataStoreManager(application)

    private val _uiState = MutableStateFlow(RecipeDetailsUiState())
    val uiState: StateFlow<RecipeDetailsUiState> = _uiState.asStateFlow()

    private val recipeId: Int = getRecipeIdFromSavedState()

    init {
        if (recipeId != -1) {
            subscribeToRecipeFlow()
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
        return savedStateHandle.get<Int>(Constants.PARAM_RECIPE_ID)
            ?: savedStateHandle.get<String>("recipeId")?.toIntOrNull()
            ?: -1
    }

    private fun subscribeToRecipeFlow() {
        viewModelScope.launch {
            loadRecipeWithFallback(recipeId)
                .onStart {
                    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                }
                .catch { exception ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = "Ошибка загрузки рецепта: ${exception.localizedMessage}"
                        )
                    }
                }
                .collect { recipe ->
                    if (recipe != null) {
                        val savedPortions = savedStateHandle.get<Int>("currentPortions")
                        val initialPortions = savedPortions ?: recipe.servings

                        _uiState.update { currentState ->
                            currentState.copy(
                                recipe = recipe,
                                currentPortions = initialPortions,
                                isLoading = false,
                                errorMessage = null
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
                }
        }
    }

    private suspend fun loadRecipeWithFallback(recipeId: Int) =
        flow<RecipeUiModel?> {
            val recipeFromFlow = repository.getRecipe(recipeId)
                .map { it?.toUiModel() }
                .firstOrNull { it != null }

            if (recipeFromFlow != null) {
                emit(recipeFromFlow)
                return@flow
            }

            val forcedRecipe = repository.forceLoadRecipe(recipeId)?.toUiModel()
            if (forcedRecipe != null) {
                emit(forcedRecipe)
                return@flow
            }

            emit(null)
        }

    private fun setupReactiveSubscriptions() {
        viewModelScope.launch {
            combine(
                favoriteManager.isFavoriteFlow(recipeId),
                _uiState
            ) { isFavorite, currentState ->
                currentState.copy(
                    isFavorite = isFavorite,
                    isFavoriteOperationInProgress = false
                )
            }.collect { newState ->
                _uiState.update { newState }
            }
        }
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
            savedStateHandle["currentPortions"] = newPortions

            _uiState.update { currentState ->
                currentState.copy(currentPortions = newPortions)
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    companion object {
        fun provideFactory(
            application: Application,
            recipeId: Int,
            repository: RecipesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(RecipeDetailsViewModel::class.java)) {
                    val savedStateHandle = SavedStateHandle().apply {
                        set(Constants.PARAM_RECIPE_ID, recipeId)
                    }
                    return RecipeDetailsViewModel(
                        savedStateHandle = savedStateHandle,
                        application = application,
                        repository = repository
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}