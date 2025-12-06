package com.yourcompany.recipecomposeapp.recipes.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.recipecomposeapp.categories.data.RecipesRepositoryStub
import com.yourcompany.recipecomposeapp.recipes.presentation.model.RecipesUiState
import com.yourcompany.recipecomposeapp.recipes.presentation.model.toUiModel
import com.yourcompany.recipecomposeapp.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class RecipesViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipesUiState.Default)
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    private val categoryId: Int
    private val categoryTitle: String
    private val categoryImageUrl: String

    init {

        categoryId = savedStateHandle.get<Int>(Constants.KEY_CATEGORY_ID) ?: -1

        categoryTitle = decodeParameter(
            savedStateHandle.get<String>(Constants.KEY_CATEGORY_TITLE),
            defaultValue = "Рецепты"
        )

        categoryImageUrl = decodeParameter(
            savedStateHandle.get<String>(Constants.KEY_CATEGORY_IMAGE_URL),
            defaultValue = ""
        )

        _uiState.update { state ->
            state.copy(
                categoryTitle = categoryTitle,
                categoryImageUrl = categoryImageUrl
            )
        }

        loadRecipes(categoryId)
    }

    private fun decodeParameter(encodedParameter: String?, defaultValue: String): String {
        return try {
            when {
                encodedParameter == null || encodedParameter.isEmpty() -> defaultValue
                else -> URLDecoder.decode(encodedParameter, StandardCharsets.UTF_8.toString())
            }
        } catch (e: Exception) {

            println("Ошибка декодирования параметра: ${e.message}")
            defaultValue
        }
    }

    private fun loadRecipes(categoryId: Int) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                val recipesDto = RecipesRepositoryStub.getRecipesByCategoryId(categoryId)
                val recipes = recipesDto.map { it.toUiModel() }

                _uiState.update { state ->
                    state.copy(
                        recipes = recipes,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = "Не удалось загрузить рецепты: ${e.localizedMessage ?: "Неизвестная ошибка"}"
                    )
                }
            }
        }
    }

    fun retry() {
        loadRecipes(categoryId)
    }
}