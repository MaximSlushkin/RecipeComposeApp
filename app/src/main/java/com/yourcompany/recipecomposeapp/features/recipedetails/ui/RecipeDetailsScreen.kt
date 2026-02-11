package com.yourcompany.recipecomposeapp.features.recipedetails.ui

import android.app.Application
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourcompany.recipecomposeapp.R
import com.yourcompany.recipecomposeapp.data.repository.RecipesRepository
import com.yourcompany.recipecomposeapp.core.ui.ScreenHeader
import com.yourcompany.recipecomposeapp.core.ui.components.ingredients.IngredientItem
import com.yourcompany.recipecomposeapp.core.ui.components.ingredients.InstructionItem
import com.yourcompany.recipecomposeapp.core.ui.components.ingredients.PortionsSlider
import com.yourcompany.recipecomposeapp.core.ui.components.ingredients.presentation.model.IngredientUiModel
import com.yourcompany.recipecomposeapp.features.recipes.presentation.model.RecipeUiModel
import com.yourcompany.recipecomposeapp.features.recipedetails.presentation.RecipeDetailsViewModel
import com.yourcompany.recipecomposeapp.ui.theme.RecipesAppTheme
import com.yourcompany.recipecomposeapp.core.utils.Constants
import com.yourcompany.recipecomposeapp.core.utils.ShareUtils

@Composable
fun RecipeDetailsScreen(
    recipeId: Int,
    repository: RecipesRepository,
    modifier: Modifier = Modifier
) {
    val application = LocalContext.current.applicationContext as? Application
    if (application == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(dimensionResource(R.dimen.mainPadding)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ошибка инициализации приложения",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val viewModel: RecipeDetailsViewModel = remember(recipeId) {

        val savedStateHandle = SavedStateHandle().apply {
            set(Constants.PARAM_RECIPE_ID, recipeId)
        }
        RecipeDetailsViewModel(
            application = application,
            savedStateHandle = savedStateHandle,
            repository = repository
        )
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            uiState.isLoading -> LoadingState()
            uiState.hasError -> ErrorState(
                errorMessage = uiState.errorMessage ?: "Произошла неизвестная ошибка",
                onRetry = {
                    viewModel.clearError()
                }
            )

            uiState.recipe != null -> {
                val currentRecipe = uiState.recipe ?: return
                RecipeContent(
                    recipe = currentRecipe,
                    currentPortions = uiState.currentPortions,
                    isFavorite = uiState.isFavorite,
                    isFavoriteOperationInProgress = uiState.isFavoriteOperationInProgress,
                    onPortionsChanged = { newPortions ->
                        viewModel.updatePortions(newPortions)
                    },
                    onShareClick = {
                        ShareUtils.shareRecipe(
                            context,
                            currentRecipe.id,
                            currentRecipe.title
                        )
                    },
                    onFavoriteToggle = { viewModel.toggleFavorite() },
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> EmptyState()
        }
    }
}

@Composable
fun RecipeContent(
    recipe: RecipeUiModel,
    currentPortions: Int,
    isFavorite: Boolean,
    isFavoriteOperationInProgress: Boolean,
    onPortionsChanged: (Int) -> Unit,
    onShareClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val multiplier = if (recipe.servings > 0) {
        currentPortions.toFloat() / recipe.servings.toFloat()
    } else {
        1.0f
    }

    val adjustedIngredients by remember(recipe.ingredients, multiplier) {
        derivedStateOf {
            recipe.ingredients.map { ingredient ->
                adjustIngredientForPortions(ingredient, multiplier)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            header = recipe.title,
            imageUrl = "",
            imageRes = R.drawable.bcg_categories,
            showShareButton = true,
            onShareClick = onShareClick,
            isFavorite = isFavorite,
            onFavoriteToggle = onFavoriteToggle,
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
                    text = "Ингредиенты".uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.mainPadding)
                    )
                )
            }

            item {
                PortionsSlider(
                    currentPortions = currentPortions,
                    onPortionsChanged = onPortionsChanged,
                )
            }

            items(adjustedIngredients) { ingredient ->
                IngredientItem(
                    ingredient = ingredient,
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.mainPadding)
                    )
                )
            }

            item {
                Text(
                    text = "Способ приготовления".uppercase(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
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
private fun ErrorState(
    errorMessage: String,
    onRetry: () -> Unit
) {
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

private fun adjustIngredientForPortions(
    ingredient: IngredientUiModel,
    multiplier: Float
): IngredientUiModel {
    return try {
        val originalAmount = ingredient.amount

        val (numberPart, unitPart) = parseAmount(originalAmount)

        if (numberPart != null) {
            val adjustedNumber = numberPart * multiplier
            val formattedAmount = formatAdjustedAmount(adjustedNumber, unitPart)

            ingredient.copy(amount = formattedAmount)
        } else {
            ingredient
        }
    } catch (e: Exception) {
        ingredient
    }
}

private fun parseAmount(amount: String): Pair<Float?, String> {
    val trimmedAmount = amount.trim()

    val numberRegex = """^([\d.,]+)\s*(.*)""".toRegex()
    val matchResult = numberRegex.find(trimmedAmount)

    return if (matchResult != null) {
        val numberStr = matchResult.groupValues[1].replace(',', '.')
        val unit = matchResult.groupValues[2].trim()

        try {
            numberStr.toFloat() to unit
        } catch (e: NumberFormatException) {
            null to trimmedAmount
        }
    } else {
        null to trimmedAmount
    }
}

private fun formatAdjustedAmount(amount: Float, unit: String): String {
    return if (amount == amount.toInt().toFloat()) {
        "${amount.toInt()} $unit".trim()
    } else {
        "${"%.1f".format(amount)} $unit".trim()
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
            IngredientUiModel("Говяжий фарш", "500 г"),
            IngredientUiModel("Булочка для бургера", "4 шт"),
            IngredientUiModel("Сыр Чеддер", "200 г"),
            IngredientUiModel("Помидор", "1 шт"),
            IngredientUiModel("Оливковое масло", "2 ст. л."),
            IngredientUiModel("Соль и перец", "по вкусу")
        ),
        method = listOf(
            "1. Сформируйте котлеты из фарша",
            "2. Обжарьте котлеты на сковороде до золотистой корочки",
            "3. Поджарьте булочки на гриле",
            "4. Соберите бургер: булочка, котлета, сыр, овощи"
        ),
        servings = 4
    )

    RecipesAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            RecipeContent(
                recipe = sampleRecipe,
                currentPortions = 4,
                isFavorite = true,
                isFavoriteOperationInProgress = false,
                onPortionsChanged = { },
                onShareClick = { },
                onFavoriteToggle = { }
            )
        }
    }
}