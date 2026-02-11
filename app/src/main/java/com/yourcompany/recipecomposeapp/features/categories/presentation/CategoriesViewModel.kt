package com.yourcompany.recipecomposeapp.features.categories.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yourcompany.recipecomposeapp.data.repository.RecipesRepository
import com.yourcompany.recipecomposeapp.features.categories.presentation.model.CategoriesUiState
import com.yourcompany.recipecomposeapp.features.categories.presentation.model.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val repository: RecipesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())

    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getCategories()
                .onStart {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                }
                .catch { exception ->

                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = "Ошибка загрузки категорий: ${exception.message ?: "Неизвестная ошибка"}",
                            isEmpty = true
                        )
                    }
                }
                .collectLatest { categoriesDto ->
                    val categories = categoriesDto.map { it.toUiModel() }

                    _uiState.update { currentState ->
                        currentState.copy(
                            categories = categories,
                            isLoading = false,
                            error = null,
                            isEmpty = categories.isEmpty()
                        )
                    }
                }
        }
    }

    fun refreshCategories() {
        viewModelScope.launch {
            try {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = true,
                        error = null
                    )
                }

                repository.refreshCategories()


            } catch (e: Exception) {

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Ошибка обновления: ${e.message ?: "Неизвестная ошибка"}"
                    )
                }
            }
        }
    }

    fun retry() {
        loadCategories()
    }

    companion object {
        fun provideFactory(repository: RecipesRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CategoriesViewModel(repository) as T
                }
            }
        }
    }
}