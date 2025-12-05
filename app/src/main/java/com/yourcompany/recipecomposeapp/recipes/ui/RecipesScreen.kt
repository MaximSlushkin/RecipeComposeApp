package com.yourcompany.recipecomposeapp.recipes.ui

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourcompany.recipecomposeapp.R
import com.yourcompany.recipecomposeapp.core.ui.ScreenHeader
import com.yourcompany.recipecomposeapp.recipes.presentation.model.RecipeUiModel
import com.yourcompany.recipecomposeapp.categories.data.RecipesRepositoryStub
import com.yourcompany.recipecomposeapp.recipes.presentation.model.toUiModel

@Composable
fun RecipesScreen(
    categoryId: Int,
    categoryTitle: String,
    modifier: Modifier = Modifier,
    onRecipeClick: (Int, RecipeUiModel) -> Unit = { _, _ -> }
) {
    var recipes by remember { mutableStateOf<List<RecipeUiModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = categoryId) {
        isLoading = true
        errorMessage = null

        try {
            val recipesDto = RecipesRepositoryStub.getRecipesByCategoryId(categoryId)
            recipes = recipesDto.map { it.toUiModel() }
        } catch (e: Exception) {
            errorMessage = "Не удалось загрузить рецепты: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            header = categoryTitle,
            imageRes = R.drawable.bcg_categories,
            modifier = Modifier
        )

        when {
            isLoading -> {
                LoadingState()
            }
            errorMessage != null -> {
                ErrorState(errorMessage = errorMessage!!)
            }
            recipes.isEmpty() -> {
                EmptyState()
            }
            else -> {
                RecipesList(
                    recipes = recipes,
                    onRecipeClick = onRecipeClick
                )
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
                text = "Загрузка рецептов...",
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
    RecipesScreen(
        categoryId = 0,
        categoryTitle = "Бургеры"
    )
}