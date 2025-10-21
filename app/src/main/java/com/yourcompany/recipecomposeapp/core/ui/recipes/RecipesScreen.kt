package com.yourcompany.recipecomposeapp.core.ui.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.yourcompany.recipecomposeapp.R
import com.yourcompany.recipecomposeapp.core.ui.ScreenHeader

@Composable
fun RecipesScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ScreenHeader(
            header = "Рецепты",
            imageRes = R.drawable.bcg_categories,
            modifier = Modifier
        )
        RecipesContent()
    }
}

@Composable
fun RecipesContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text("Скоро здесь будет список рецептов")
    }
}

@Preview(showBackground = true)
@Composable
fun RecipesScreenPreview() {
    RecipesScreen()
}