package com.yourcompany.recipecomposeapp.features.favorites.ui

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourcompany.recipecomposeapp.R
import com.yourcompany.recipecomposeapp.core.ui.ScreenHeader
import com.yourcompany.recipecomposeapp.data.repository.RecipesRepository
import com.yourcompany.recipecomposeapp.features.favorites.presentation.FavoritesViewModel
import com.yourcompany.recipecomposeapp.features.favorites.presentation.model.FavoritesUiState
import com.yourcompany.recipecomposeapp.features.recipes.ui.RecipeItem
import com.yourcompany.recipecomposeapp.features.recipes.presentation.model.RecipeUiModel
import com.yourcompany.recipecomposeapp.ui.theme.RecipesAppTheme

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    repository: RecipesRepository,
    onRecipeClick: (Int) -> Unit = { }
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val viewModel: FavoritesViewModel = remember {
        FavoritesViewModel(
            application = application,
            repository = repository
        )
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            header = "Избранное",
            imageUrl = "",
            imageRes = R.drawable.bcg_categories,
            modifier = Modifier
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    LoadingState()
                }

                uiState.hasError -> {
                    ErrorState(
                        errorMessage = uiState.errorMessage ?: "Произошла неизвестная ошибка",
                        onRetry = { viewModel.refresh() }
                    )
                }

                uiState.isEmpty -> {
                    EmptyState()
                }

                else -> {
                    RecipesList(
                        recipes = uiState.favoriteRecipes,
                        onRecipeClick = onRecipeClick
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipesList(
    recipes: List<RecipeUiModel>,
    onRecipeClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            vertical = dimensionResource(R.dimen.mainPadding)
        ),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.cardPadding)
        )
    ) {
        items(
            items = recipes,
            key = { it.id }
        ) { recipe ->
            RecipeItem(
                recipe = recipe,
                onClick = { recipeId ->
                    onRecipeClick(recipeId)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.mainPadding))
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Загрузка избранных рецептов...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun ErrorState(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )

            Button(
                onClick = onRetry
            ) {
                Text("Повторить")
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Здесь появятся рецепты, которые вы добавите в избранное",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    RecipesAppTheme {
        val viewModel: FavoritesViewModel = viewModel()
        val uiState = FavoritesUiState(
            favoriteRecipes = listOf(
                RecipeUiModel(
                    id = 1,
                    title = "Классический бургер",
                    imageUrl = "",
                    ingredients = emptyList(),
                    method = emptyList()
                )
            ),
            isLoading = false,
            isEmpty = false
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            ScreenHeader(
                header = "Избранное",
                imageUrl = "",
                imageRes = R.drawable.bcg_categories,
                modifier = Modifier
            )

            RecipesList(
                recipes = uiState.favoriteRecipes,
                onRecipeClick = { }
            )
        }
    }
}