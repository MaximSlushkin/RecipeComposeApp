package com.yourcompany.recipecomposeapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yourcompany.recipecomposeapp.core.ui.navigation.BottomNavigation
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
            )
        }
        ) { innerPadding ->
            when (state) {
                ScreenId.CATEGORIES -> Categories(
                    modifier = Modifier.padding(innerPadding)
                )

                ScreenId.FAVORITES -> Favorites(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun Categories(modifier: Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(text = "Категории", modifier = modifier)
    }
}

@Composable
private fun Favorites(modifier: Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(text = "Избранное", modifier = modifier)
    }
}