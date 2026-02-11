package com.yourcompany.recipecomposeapp.features.recipes.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.recipecomposeapp.data.repository.RecipesRepository
import com.yourcompany.recipecomposeapp.features.recipes.presentation.model.RecipesUiState
import com.yourcompany.recipecomposeapp.features.recipes.presentation.model.toUiModel
import com.yourcompany.recipecomposeapp.core.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class RecipesViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: RecipesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipesUiState.Companion.Default)
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    private val categoryId: Int = savedStateHandle.get<Int>(Constants.KEY_CATEGORY_ID) ?: -1
    private val categoryTitle: String = decodeParameter(
        savedStateHandle.get<String>(Constants.KEY_CATEGORY_TITLE),
        defaultValue = "Рецепты"
    )
    private val categoryImageUrl: String = decodeParameter(
        savedStateHandle.get<String>(Constants.KEY_CATEGORY_IMAGE_URL),
        defaultValue = ""
    )

    init {
        _uiState.update { state ->
            state.copy(
                categoryTitle = categoryTitle,
                categoryImageUrl = categoryImageUrl
            )
        }

        loadRecipes()
    }

    private fun decodeParameter(encodedParameter: String?, defaultValue: String): String {
        return try {
            when {
                encodedParameter == null || encodedParameter.isEmpty() -> defaultValue
                else -> URLDecoder.decode(encodedParameter, StandardCharsets.UTF_8.toString())
            }
        } catch (_: Exception) {
            defaultValue
        }
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            repository.getRecipesByCategory(categoryId)
                .onStart {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                    }
                }
                .catch { exception ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = "Ошибка загрузки рецептов: ${exception.message ?: "Неизвестная ошибка"}"
                        )
                    }
                }
                .collectLatest { recipesDto ->
                    val recipes = recipesDto.map { it.toUiModel() }

                    _uiState.update { state ->
                        state.copy(
                            recipes = recipes,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun refreshRecipes() {
        viewModelScope.launch {
            try {
                _uiState.update { state ->
                    state.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                }

                repository.refreshRecipes(categoryId)

            } catch (e: Exception) {

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = "Ошибка обновления: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun retry() {
        loadRecipes()
    }
}