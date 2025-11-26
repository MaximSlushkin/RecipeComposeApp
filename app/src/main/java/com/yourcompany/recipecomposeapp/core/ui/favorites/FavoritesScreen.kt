package com.yourcompany.recipecomposeapp.core.ui.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.yourcompany.recipecomposeapp.R
import com.yourcompany.recipecomposeapp.core.ui.recipes.RecipeItem
import com.yourcompany.recipecomposeapp.data.model.RecipeUiModel
import com.yourcompany.recipecomposeapp.data.model.toUiModel
import com.yourcompany.recipecomposeapp.data.repository.RecipesRepositoryStub
import com.yourcompany.recipecomposeapp.ui.theme.RecipesAppTheme
import com.yourcompany.recipecomposeapp.utils.FavoriteDataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    onRecipeClick: (Int, RecipeUiModel) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current

    val favoriteManager = remember { FavoriteDataStoreManager(context) }

    var favoriteRecipes by remember { mutableStateOf<List<RecipeUiModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null

        try {
            val favorites = loadFavoriteRecipes(favoriteManager)
            favoriteRecipes = favorites
        } catch (e: Exception) {
            errorMessage = "Ошибка загрузки избранных рецептов: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.mainPadding))
    ) {
        Text(
            text = "Избранные рецепты",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.mainPadding))
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                favoriteRecipes.isEmpty() -> {
                    Text("Нет избранных рецептов")
                }
                else -> {
                    LazyColumn {
                        items(favoriteRecipes, key = { it.id }) { recipe ->
                            RecipeItem(
                                recipe = recipe,
                                onClick = { recipeId, recipeObj -> onRecipeClick(recipeId, recipeObj) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = dimensionResource(R.dimen.mainPadding),
                                        vertical = dimensionResource(R.dimen.cardPadding) / 2
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

private suspend fun loadFavoriteRecipes(favoriteManager: FavoriteDataStoreManager): List<RecipeUiModel> {
    val favoriteIds = favoriteManager.getAllFavorites()
    if (favoriteIds.isEmpty()) {
        return emptyList()
    }

    val allRecipes = RecipesRepositoryStub.getCategories().flatMap { category ->
        RecipesRepositoryStub.getRecipesByCategoryId(category.id)
    }

    return favoriteIds.mapNotNull { recipeIdStr ->
        val recipeId = recipeIdStr.toIntOrNull()
        recipeId?.let { id ->
            allRecipes.find { it.id == id }?.toUiModel()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    RecipesAppTheme {
        FavoritesScreen()
    }
}