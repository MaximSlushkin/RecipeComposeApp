package com.yourcompany.recipecomposeapp.core.ui.favorites.ui

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourcompany.recipecomposeapp.R
import com.yourcompany.recipecomposeapp.core.ui.ScreenHeader
import com.yourcompany.recipecomposeapp.core.ui.recipes.ui.RecipeItem
import com.yourcompany.recipecomposeapp.core.ui.recipes.presentation.model.RecipeUiModel
import com.yourcompany.recipecomposeapp.core.ui.categories.presentation.model.toUiModel
import com.yourcompany.recipecomposeapp.core.ui.categories.data.RecipesRepositoryStub
import com.yourcompany.recipecomposeapp.core.ui.recipes.presentation.model.toUiModel
import com.yourcompany.recipecomposeapp.ui.theme.RecipesAppTheme
import com.yourcompany.recipecomposeapp.utils.FavoriteDataStoreManager
import kotlinx.coroutines.flow.map

@Composable
fun FavoritesScreen(
    favoriteManager: FavoriteDataStoreManager,
    recipesRepository: RecipesRepositoryStub,
    modifier: Modifier = Modifier,
    onRecipeClick: (Int, RecipeUiModel) -> Unit = { _, _ -> }
) {
    val allRecipes = remember {
        recipesRepository.getCategories().flatMap { category ->
            recipesRepository.getRecipesByCategoryId(category.id)
        }
    }

    val favoriteRecipes by favoriteManager.getFavoriteIdsFlow()
        .map { favoriteIds ->
            favoriteIds.mapNotNull { recipeIdStr ->
                val recipeId = recipeIdStr.toIntOrNull()
                recipeId?.let { id ->
                    allRecipes.find { it.id == id }?.toUiModel()
                }
            }
        }
        .collectAsState(initial = emptyList())

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            header = "Избранное",
            imageRes = R.drawable.bcg_categories,
            modifier = Modifier
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    LoadingState()
                }
                errorMessage != null -> {
                    ErrorState(errorMessage = errorMessage ?: "Произошла неизвестная ошибка")
                }
                favoriteRecipes.isEmpty() -> {
                    EmptyState()
                }
                else -> {
                    RecipesList(
                        recipes = favoriteRecipes,
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
    onRecipeClick: (Int, RecipeUiModel) -> Unit
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
                onClick = { recipeId, recipeObj -> onRecipeClick(recipeId, recipeObj) },
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
private fun ErrorState(errorMessage: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
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
    val context = LocalContext.current
    RecipesAppTheme {
        FavoritesScreen(
            favoriteManager = FavoriteDataStoreManager(context),
            recipesRepository = RecipesRepositoryStub,
            onRecipeClick = { _, _ -> }
        )
    }
}