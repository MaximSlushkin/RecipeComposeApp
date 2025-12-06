package com.yourcompany.recipecomposeapp.categories.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.recipecomposeapp.categories.data.RecipesRepositoryStub
import com.yourcompany.recipecomposeapp.categories.presentation.model.CategoriesUiState
import com.yourcompany.recipecomposeapp.categories.presentation.model.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val repository: RecipesRepositoryStub = RecipesRepositoryStub
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())

    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {

        _uiState.update { currentState ->
            currentState.copy(
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            try {

                kotlinx.coroutines.delay(500)

                val categories = repository.getCategories().map { it.toUiModel() }

                _uiState.update { currentState ->
                    currentState.copy(
                        categories = categories,
                        isLoading = false,
                        error = null,
                        isEmpty = categories.isEmpty()
                    )
                }

            } catch (e: Exception) {

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Не удалось загрузить категории: ${e.message ?: "Неизвестная ошибка"}",
                        isEmpty = true
                    )
                }
            }
        }
    }

    fun refreshCategories() {
        loadCategories()
    }
}