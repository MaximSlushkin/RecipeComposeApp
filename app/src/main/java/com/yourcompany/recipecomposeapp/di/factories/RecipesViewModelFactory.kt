package com.yourcompany.recipecomposeapp.di.factories

import androidx.lifecycle.SavedStateHandle
import com.yourcompany.recipecomposeapp.data.repository.RecipesRepository
import com.yourcompany.recipecomposeapp.features.recipes.presentation.RecipesViewModel

class RecipesViewModelFactory(
    private val savedStateHandle: SavedStateHandle,
    private val repository: RecipesRepository
) : Factory<RecipesViewModel> {

    override fun create(): RecipesViewModel {
        return RecipesViewModel(
            savedStateHandle = savedStateHandle,
            repository = repository
        )
    }
}