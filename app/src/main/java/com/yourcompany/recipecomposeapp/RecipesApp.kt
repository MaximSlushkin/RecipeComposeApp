package com.yourcompany.recipecomposeapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.yourcompany.recipecomposeapp.core.ui.categories.CategoriesScreen
import com.yourcompany.recipecomposeapp.core.ui.favorites.FavoritesScreen
import com.yourcompany.recipecomposeapp.core.ui.navigation.BottomNavigation
import com.yourcompany.recipecomposeapp.core.ui.recipes.RecipesScreen
import com.yourcompany.recipecomposeapp.data.repository.RecipesRepositoryStub
import com.yourcompany.recipecomposeapp.ui.theme.RecipesAppTheme

@Composable
fun RecipesApp() {
    RecipesAppTheme(
    ) {
        var state by remember { mutableStateOf(ScreenId.CATEGORIES) }
        Scaffold(bottomBar = {
            BottomNavigation(
                onCategoriesClick = { state = ScreenId.CATEGORIES },
                onFavoritesClick = { state = ScreenId.FAVORITES },
                onRecipesClick = { state = ScreenId.RECIPES },
            )
        }
        ) { innerPadding ->
            when (state) {
                ScreenId.CATEGORIES -> {
                    val categories = RecipesRepositoryStub.getCategories()
                    CategoriesScreen(
                        modifier = Modifier.padding(innerPadding),
                        categories = categories
                    )
                }

                ScreenId.FAVORITES -> FavoritesScreen(
                    modifier = Modifier.padding(innerPadding)
                )

                ScreenId.RECIPES -> RecipesScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}