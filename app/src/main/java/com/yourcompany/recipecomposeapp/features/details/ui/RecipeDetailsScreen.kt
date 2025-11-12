package com.yourcompany.recipecomposeapp.features.details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.yourcompany.recipecomposeapp.R
import com.yourcompany.recipecomposeapp.core.ui.ScreenHeader
import com.yourcompany.recipecomposeapp.data.model.RecipeUiModel

@Composable
fun RecipeDetailsScreen(
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
                vertical = dimensionResource(R.dimen.mainPadding),
                horizontal = dimensionResource(R.dimen.mainPadding)
            ),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.mainPadding)
            )
        ) {
            item {
                Text(
                    text = "Ингредиенты",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(recipe.ingredients) { ingredient ->
                Text(
                    text = "• ${ingredient.name} - ${ingredient.amount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(
                        vertical = dimensionResource(R.dimen.cardPadding)
                    )
                )
            }

            item {
                Text(
                    text = "Способ приготовления",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.mainPadding))
                )
            }

            items(recipe.method) { step ->
                Text(
                    text = step,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(
                        vertical = dimensionResource(R.dimen.cardPadding)
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeDetailsScreenPreview() {
    val sampleRecipe = RecipeUiModel(
        id = 1,
        title = "Классический бургер",
        imageUrl = "",
        ingredients = listOf(
            com.yourcompany.recipecomposeapp.data.model.IngredientUiModel("Говяжий фарш", "500 г"),
            com.yourcompany.recipecomposeapp.data.model.IngredientUiModel("Булочка", "1 шт"),
            com.yourcompany.recipecomposeapp.data.model.IngredientUiModel("Сыр", "2 ломтика")
        ),
        method = listOf(
            "1. Сформируйте котлеты из фарша",
            "2. Обжарьте котлеты на сковороде",
            "3. Соберите бургер"
        )
    )

    RecipeDetailsScreen(recipe = sampleRecipe)
}