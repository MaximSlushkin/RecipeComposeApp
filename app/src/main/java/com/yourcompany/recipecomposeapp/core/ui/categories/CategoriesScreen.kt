package com.yourcompany.recipecomposeapp.core.ui.categories

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourcompany.recipecomposeapp.R

@Composable
fun ScreenHeader(header: String, imageRes: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(224.dp),
        contentAlignment = Alignment.BottomStart,
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(dimensionResource(R.dimen.mainPadding)),
            shape = RoundedCornerShape(
                dimensionResource(R.dimen.mainPadding)
            ),
            color = MaterialTheme.colorScheme.background,
        ) {
            Text(
                text = header.uppercase(),
                modifier = Modifier.padding(dimensionResource(R.dimen.headerPadding)),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun CategoriesContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text("Здесь будут категории")
    }
}

@Composable
fun CategoriesScreen(modifier: Modifier) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ScreenHeader(
            header = "Категории",
            imageRes = R.drawable.bcg_categories
        )
        CategoriesContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenHeaderPreview() {
    ScreenHeader(
        header = "Категории",
        imageRes = R.drawable.bcg_categories
    )
}
