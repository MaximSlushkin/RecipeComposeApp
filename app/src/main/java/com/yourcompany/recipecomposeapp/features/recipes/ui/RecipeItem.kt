package com.yourcompany.recipecomposeapp.features.recipes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourcompany.recipecomposeapp.R
import com.yourcompany.recipecomposeapp.core.ui.RecipeImage
import com.yourcompany.recipecomposeapp.features.recipes.presentation.model.RecipeUiModel

@Composable
fun RecipeItem(
    recipe: RecipeUiModel,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(recipe.id) },
        shape = RoundedCornerShape(
            size = dimensionResource(R.dimen.basicCornerRadius)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            RecipeImage(
                imageUrl = recipe.imageUrl,
                contentDescription = "Изображение рецепта: ${recipe.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                cornerRadius = dimensionResource(R.dimen.basicCornerRadius).value,
                showLoadingIndicator = true
            )

            Text(
                text = recipe.title.uppercase(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.cardPadding)),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start,
                maxLines = 2
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeItemPreview() {
    val recipe = RecipeUiModel(
        id = 1,
        title = "Классический бургер",
        imageUrl = "https://recipes.androidsprint.ru/api/images/burger.jpg",
        ingredients = emptyList(),
        method = emptyList()
    )

    RecipeItem(
        recipe = recipe,
        onClick = { recipeId -> }
    )
}