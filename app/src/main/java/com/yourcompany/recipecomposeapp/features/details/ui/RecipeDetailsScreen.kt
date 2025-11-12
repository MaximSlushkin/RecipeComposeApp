package com.yourcompany.recipecomposeapp.features.details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourcompany.recipecomposeapp.R
import com.yourcompany.recipecomposeapp.core.ui.ScreenHeader
import com.yourcompany.recipecomposeapp.core.ui.ingredients.IngredientItem
import com.yourcompany.recipecomposeapp.core.ui.ingredients.InstructionItem
import com.yourcompany.recipecomposeapp.data.model.RecipeUiModel
import com.yourcompany.recipecomposeapp.data.model.toUiModel
import com.yourcompany.recipecomposeapp.data.repository.RecipesRepositoryStub

@Composable
fun RecipeDetailsScreen(
    recipeId: Int,
    modifier: Modifier = Modifier
) {
    var recipe by remember { mutableStateOf<RecipeUiModel?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = recipeId) {
        isLoading = true
        errorMessage = null

        try {
            val allRecipes = RecipesRepositoryStub.getRecipesByCategoryId(0)
            val foundRecipe = allRecipes.find { it.id == recipeId }?.toUiModel()

            if (foundRecipe != null) {
                recipe = foundRecipe
            } else {
                errorMessage = "Рецепт не найден"
            }
        } catch (e: Exception) {
            errorMessage = "Ошибка загрузки: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    when {
        isLoading -> {
            LoadingState()
        }
        errorMessage != null -> {
            ErrorState(errorMessage = errorMessage!!)
        }
        recipe != null -> {
            RecipeContent(recipe = recipe!!, modifier = modifier)
        }
        else -> {
            EmptyState()
        }
    }
}

@Composable
private fun RecipeContent(
    recipe: RecipeUiModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            header = recipe.title,
            imageRes = R.drawable.bcg_categories,
            modifier = Modifier
        )

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
            item {
                Text(
                    text = "Ингредиенты",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.mainPadding)
                    )
                )
            }

            items(recipe.ingredients) { ingredient ->
                IngredientItem(
                    ingredient = ingredient,
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.mainPadding)
                    )
                )
            }

            item {
                Text(
                    text = "Способ приготовления",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.mainPadding),
                        vertical = dimensionResource(R.dimen.mainPadding)
                    )
                )
            }

            items(recipe.method) { step ->
                InstructionItem(
                    step = step,
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.mainPadding)
                    )
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(dimensionResource(R.dimen.mainPadding)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(
            text = "Загрузка рецепта...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun ErrorState(errorMessage: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(dimensionResource(R.dimen.mainPadding)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(dimensionResource(R.dimen.mainPadding)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Рецепт не найден",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showBackground = true, name = "Recipe Details Loaded")
@Composable
fun RecipeDetailsScreenPreview() {
    val sampleRecipe = RecipeUiModel(
        id = 1,
        title = "Классический бургер",
        imageUrl = "",
        ingredients = listOf(
            com.yourcompany.recipecomposeapp.data.model.IngredientUiModel("Говяжий фарш", "500 г"),
            com.yourcompany.recipecomposeapp.data.model.IngredientUiModel("Булочка для бургера", "4 шт"),
            com.yourcompany.recipecomposeapp.data.model.IngredientUiModel("Сыр Чеддер", "200 г"),
            com.yourcompany.recipecomposeapp.data.model.IngredientUiModel("Помидор", "1 шт")
        ),
        method = listOf(
            "1. Сформируйте котлеты из фарша",
            "2. Обжарьте котлеты на сковороде до золотистой корочки",
            "3. Поджарьте булочки на гриле",
            "4. Соберите бургер: булочка, котлета, сыр, овощи"
        )
    )

    RecipeContent(recipe = sampleRecipe)
}

@Preview(showBackground = true, name = "Recipe Details Loading")
@Composable
fun RecipeDetailsScreenLoadingPreview() {
    LoadingState()
}

@Preview(showBackground = true, name = "Recipe Details Error")
@Composable
fun RecipeDetailsScreenErrorPreview() {
    ErrorState(errorMessage = "Рецепт не найден")
}