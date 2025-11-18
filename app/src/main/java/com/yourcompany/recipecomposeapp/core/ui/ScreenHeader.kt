package com.yourcompany.recipecomposeapp.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourcompany.recipecomposeapp.R
import com.yourcompany.recipecomposeapp.core.ui.favorites.FavoriteButton

@Composable
fun ScreenHeader(
    header: String,
    imageRes: Int,
    showShareButton: Boolean = false,
    onShareClick: () -> Unit = {},
    isFavorite: Boolean = false,
    onFavoriteToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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

        if (showShareButton) {

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(dimensionResource(R.dimen.mainPadding)),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier.size(48.dp),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_share),
                        contentDescription = "Поделиться",
                        modifier = Modifier.size(40.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(dimensionResource(R.dimen.mainPadding))
            ) {
                FavoriteButton(
                    isFavorite = isFavorite,
                    onFavoriteToggle = onFavoriteToggle,
                    modifier = Modifier
                )
            }
        }

        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(dimensionResource(R.dimen.mainPadding)),
            shape = RoundedCornerShape(
                dimensionResource(R.dimen.halfBasicCornerRadius)
            ),
            color = MaterialTheme.colorScheme.background,
        ) {
            Text(
                text = header.uppercase(),
                modifier = Modifier.padding(dimensionResource(R.dimen.headerPadding)),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenHeaderPreview() {
    ScreenHeader(
        header = "Категории",
        imageRes = R.drawable.bcg_categories,
        showShareButton = true,
        onShareClick = {},
        isFavorite = true,
        onFavoriteToggle = {}
    )
}