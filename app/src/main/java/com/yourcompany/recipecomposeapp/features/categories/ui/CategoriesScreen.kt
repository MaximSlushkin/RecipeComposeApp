package com.yourcompany.recipecomposeapp.features.categories.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourcompany.recipecomposeapp.R
import com.yourcompany.recipecomposeapp.core.ui.ScreenHeader
import com.yourcompany.recipecomposeapp.di.RecipeApplication
import com.yourcompany.recipecomposeapp.di.factories.CategoriesViewModelFactory
import com.yourcompany.recipecomposeapp.features.categories.presentation.CategoriesViewModel
import com.yourcompany.recipecomposeapp.features.categories.presentation.model.CategoryUiModel
import com.yourcompany.recipecomposeapp.ui.theme.RecipesAppTheme

@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    onCategoryClick: (Int, String, String) -> Unit = { _, _, _ -> }
) {
    val appContainer = (LocalContext.current.applicationContext as RecipeApplication).appContainer

    val viewModel: CategoriesViewModel = remember {
        CategoriesViewModelFactory(appContainer.recipesRepository).create()
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            header = "Категории",
            imageUrl = "",
            imageRes = R.drawable.bcg_categories
        )

        when {
            uiState.isLoading -> {
                LoadingState()
            }

            uiState.error != null -> {
                ErrorState(
                    errorMessage = uiState.error ?: "Ошибка",
                    onRetry = { viewModel.retry() }
                )
            }

            uiState.isEmpty -> {
                EmptyState()
            }

            uiState.categories.isNotEmpty() -> {
                CategoriesGrid(
                    categories = uiState.categories,
                    onCategoryClick = onCategoryClick
                )
            }

            else -> {
                EmptyState()
            }
        }
    }
}

@Composable
private fun CategoriesGrid(
    categories: List<CategoryUiModel>,
    onCategoryClick: (Int, String, String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.mainPadding)
        ),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.mainPadding)
        ),
        contentPadding = PaddingValues(
            dimensionResource(R.dimen.mainPadding)
        )
    ) {
        items(
            items = categories,
            key = { category -> category.id }
        ) { category ->
            CategoryItem(
                imageUrl = category.imageUrl,
                header = category.title,
                description = category.description,
                onClick = {
                    onCategoryClick(category.id, category.title, category.imageUrl)
                }
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
                text = "Загрузка категорий...",
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
            text = "Категории не найдены",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoriesScreenPreview() {
    RecipesAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                ScreenHeader(
                    header = "Категории",
                    imageUrl = "",
                    imageRes = R.drawable.bcg_categories
                )

                val mockCategories = listOf(
                    CategoryUiModel(
                        id = 0,
                        title = "Бургеры",
                        description = "Рецепты всех популярных видов бургеров",
                        imageUrl = ""
                    ),
                    CategoryUiModel(
                        id = 1,
                        title = "Десерты",
                        description = "Самые вкусные рецепты десертов",
                        imageUrl = ""
                    )
                )

                CategoriesGrid(
                    categories = mockCategories,
                    onCategoryClick = { _, _, _ -> }
                )
            }
        }
    }
}