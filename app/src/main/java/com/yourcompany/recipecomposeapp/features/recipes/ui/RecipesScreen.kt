package com.yourcompany.recipecomposeapp.features.recipes.ui

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourcompany.recipecomposeapp.R
import com.yourcompany.recipecomposeapp.core.ui.ScreenHeader
import com.yourcompany.recipecomposeapp.core.ui.components.ingredients.presentation.model.IngredientUiModel
import com.yourcompany.recipecomposeapp.features.recipes.presentation.RecipesViewModel
import com.yourcompany.recipecomposeapp.features.recipes.presentation.model.RecipeUiModel
import com.yourcompany.recipecomposeapp.ui.theme.RecipesAppTheme

@Composable
fun RecipesScreen(
    viewModel: RecipesViewModel,
    modifier: Modifier = Modifier,
    onRecipeClick: (Int) -> Unit = { }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            header = uiState.categoryTitle,
            imageUrl = uiState.categoryImageUrl,
            imageRes = R.drawable.bcg_categories,
            modifier = Modifier,
        )

        when {
            uiState.isLoading -> {
                LoadingState()
            }

            uiState.hasError -> {
                ErrorState(
                    errorMessage = uiState.errorMessage ?: "Неизвестная ошибка",
                    onRetry = viewModel::retry
                )
            }

            uiState.isEmpty -> {
                EmptyState()
            }

            else -> {
                RecipesList(
                    recipes = uiState.recipes,
                    onRecipeClick = onRecipeClick
                )
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
                text = "Загрузка рецептов...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun ErrorState(errorMessage: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) {
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
            text = "Рецепты для этой категории скоро появятся",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecipesScreenPreview() {
    // Для превью создаем заглушку ViewModel
    // В реальном приложении ViewModel будет передана из RecipesApp
    RecipesAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Создаем моковые данные для превью
            val mockRecipes = listOf(
                RecipeUiModel(
                    id = 1,
                    title = "Классический бургер",
                    imageUrl = "",
                    ingredients = listOf(
                        IngredientUiModel("Говяжий фарш", "500 г"),
                        IngredientUiModel("Булочка", "2 шт")
                    ),
                    method = listOf("1. Приготовить", "2. Подавать"),
                    servings = 4
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                ScreenHeader(
                    header = "Бургеры",
                    imageUrl = "",
                    imageRes = R.drawable.bcg_categories
                )

                RecipesList(
                    recipes = mockRecipes,
                    onRecipeClick = { }
                )
            }
        }
    }
}