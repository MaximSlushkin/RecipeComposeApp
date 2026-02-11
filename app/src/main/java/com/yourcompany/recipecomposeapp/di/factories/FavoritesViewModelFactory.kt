package com.yourcompany.recipecomposeapp.di.factories

import android.app.Application
import com.yourcompany.recipecomposeapp.data.repository.RecipesRepository
import com.yourcompany.recipecomposeapp.features.favorites.presentation.FavoritesViewModel

class FavoritesViewModelFactory(
    private val application: Application,
    private val repository: RecipesRepository
) : Factory<FavoritesViewModel> {

    override fun create(): FavoritesViewModel {
        return FavoritesViewModel(
            application = application,
            repository = repository
        )
    }
}