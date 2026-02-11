package com.yourcompany.recipecomposeapp.features.categories.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.yourcompany.recipecomposeapp.core.ui.SimpleRecipeImage

@Composable
fun CategoryItem(
    imageUrl: String,
    header: String,
    description: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .height(220.dp)
            .width(156.dp),
        shape = RoundedCornerShape(
            size = dimensionResource(R.dimen.halfBasicCornerRadius)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SimpleRecipeImage(
                imageUrl = imageUrl,
                contentDescription = "Изображение категории: $header",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                contentScale = ContentScale.Crop,
                cornerRadius = dimensionResource(R.dimen.halfBasicCornerRadius).value
            )

            Text(
                text = header.uppercase(),
                modifier = Modifier.padding(dimensionResource(R.dimen.cardPadding)),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = description,
                maxLines = 3,
                modifier = Modifier
                    .padding(
                        start = dimensionResource(R.dimen.cardPadding),
                        end = dimensionResource(R.dimen.cardPadding),
                        bottom = dimensionResource(R.dimen.cardPadding)
                    )
                    .height(50.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryItemPreview() {
    CategoryItem(
        imageUrl = "https://recipes.androidsprint.ru/api/images/burgers.png",
        header = "Бургеры",
        description = "Рецепты всех популярных видов бургеров",
        onClick = {}
    )
}