package com.yourcompany.recipecomposeapp.core.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigation(
    onCategoriesClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onRecipesClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(onClick = onCategoriesClick, modifier = Modifier.padding(16.dp)) {
            Text("Категории")
        }
        Button(onClick = onFavoritesClick, modifier = Modifier.padding(16.dp)) {
            Text("Избранное")
        }
        Button(onClick = onRecipesClick, modifier = Modifier.padding(16.dp)) {
            Text("Рецепты")
        }
    }
}