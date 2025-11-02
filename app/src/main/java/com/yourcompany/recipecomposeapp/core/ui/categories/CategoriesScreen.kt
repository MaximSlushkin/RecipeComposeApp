package com.yourcompany.recipecomposeapp.core.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.yourcompany.recipecomposeapp.R
import com.yourcompany.recipecomposeapp.core.ui.ScreenHeader
import com.yourcompany.recipecomposeapp.data.model.CategoryUiModel
import com.yourcompany.recipecomposeapp.data.model.toUiModel
import com.yourcompany.recipecomposeapp.data.repository.RecipesRepositoryStub

@Composable
fun CategoriesScreen(
    modifier: Modifier,
    categories: List<CategoryUiModel>,
    onCategoryClick: (Int, String) -> Unit = { _, _ -> }
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            header = "Категории",
            imageRes = R.drawable.bcg_categories
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.mainPadding)
            ),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.mainPadding)
            ),
            contentPadding = PaddingValues(
                dimensionResource(R.dimen.mainPadding)
            )
        ) {
            itemsIndexed(categories) { index, category ->
                CategoryItem(
                    imageRes = category.imageUrl,
                    header = category.title,
                    description = category.description,
                ) {
                    onCategoryClick(category.id, category.title)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoriesScreenPreview() {
    Scaffold() { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        val categories = RecipesRepositoryStub.getCategories().map { it.toUiModel() }
        CategoriesScreen(modifier, categories)
    }
}