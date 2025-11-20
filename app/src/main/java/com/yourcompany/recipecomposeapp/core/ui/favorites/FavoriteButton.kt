package com.yourcompany.recipecomposeapp.core.ui.favorites

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.yourcompany.recipecomposeapp.R

@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onFavoriteToggle,
        modifier = modifier.size(48.dp)
    ) {
        Crossfade(
            targetState = isFavorite,
            animationSpec = tween(durationMillis = 300),
            label = "favorite_animation"
        ) { currentlyFavorite ->

            val heartIcon = rememberVectorPainter(
                image = ImageVector.vectorResource(
                    id = if (currentlyFavorite) R.drawable.ic_heart else R.drawable.ic_heart_empty
                )
            )

            Icon(
                painter = heartIcon,
                contentDescription = if (currentlyFavorite) "Удалить из избранного" else "Добавить в избранное",
                modifier = Modifier.size(40.dp),

                tint = Color.Unspecified
            )
        }
    }
}