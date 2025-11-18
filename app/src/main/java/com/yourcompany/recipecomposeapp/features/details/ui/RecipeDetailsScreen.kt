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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.yourcompany.recipecomposeapp.core.ui.ingredients.IngredientItem
import com.yourcompany.recipecomposeapp.core.ui.ingredients.InstructionItem
import com.yourcompany.recipecomposeapp.core.ui.ingredients.PortionsSlider
import com.yourcompany.recipecomposeapp.data.model.IngredientUiModel
import com.yourcompany.recipecomposeapp.data.model.RecipeUiModel
import com.yourcompany.recipecomposeapp.data.model.toUiModel
import com.yourcompany.recipecomposeapp.data.repository.RecipesRepositoryStub
import com.yourcompany.recipecomposeapp.ui.theme.RecipesAppTheme
import com.yourcompany.recipecomposeapp.utils.ShareUtils

@Composable
fun RecipeDetailsScreen(
    recipeId: Int,
    recipe: RecipeUiModel? = null,
    modifier: Modifier = Modifier
) {
    var currentRecipe by remember { mutableStateOf(recipe) }
    var isLoading by remember { mutableStateOf(recipe == null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var currentPortions by rememberSaveable(recipeId.toString() + "_portions") {
        mutableStateOf(recipe?.servings ?: 1)
    }

    var isFavorite by rememberSaveable(recipeId.toString() + "_favorite") {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    LaunchedEffect(key1 = recipeId) {
        if (recipe == null) {
            isLoading = true
            errorMessage = null

            try {
                val allRecipes = RecipesRepositoryStub.getCategories().flatMap { category ->
                    RecipesRepositoryStub.getRecipesByCategoryId(category.id)
                }
                val foundRecipe = allRecipes.find { it.id == recipeId }?.toUiModel()

                if (foundRecipe != null) {
                    currentRecipe = foundRecipe
                    currentPortions = foundRecipe.servings
                } else {
                    errorMessage = "Рецепт не найден"
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка загрузки: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        } else {
            currentRecipe = recipe
            currentPortions = recipe.servings
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

        currentRecipe != null -> {
            RecipeContent(
                recipe = currentRecipe!!,
                currentPortions = currentPortions,
                onPortionsChanged = { newPortions -> currentPortions = newPortions },
                onShareClick = {
                    ShareUtils.shareRecipe(context, currentRecipe!!.id, currentRecipe!!.title)
                },
                isFavorite = isFavorite,
                onFavoriteToggle = {
                    isFavorite = !isFavorite
                },
                modifier = modifier
            )
        }

        else -> {
            EmptyState()
        }
    }
}

@Composable
private fun RecipeContent(
    recipe: RecipeUiModel,
    currentPortions: Int,
    onPortionsChanged: (Int) -> Unit,
    onShareClick: () -> Unit,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val multiplier = remember(currentPortions, recipe.servings) {
        currentPortions.toFloat() / recipe.servings.toFloat()
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
        RecipeContent(
            recipe = sampleRecipe,
            currentPortions = 4,
            onPortionsChanged = { },
            onShareClick = { },
            isFavorite = true,
            onFavoriteToggle = { }
        )
    }
}