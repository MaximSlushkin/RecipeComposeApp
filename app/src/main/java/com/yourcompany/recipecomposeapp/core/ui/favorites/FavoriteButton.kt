package com.yourcompany.recipecomposeapp.core.ui.favorites

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

    val heartIcon = rememberVectorPainter(
        image = ImageVector.vectorResource(
            id = if (isFavorite) R.drawable.ic_heart else R.drawable.ic_heart_empty
        )
    )

    val tintColor by animateColorAsState(
        targetValue = Color.Transparent,
        animationSpec = tween(durationMillis = 300),
        label = "favorite_color_animation"
    )

    IconButton(
        onClick = onFavoriteToggle,
        modifier = modifier.size(48.dp)
    ) {
        Icon(
            painter = heartIcon,
            contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
            modifier = Modifier.size(40.dp),
            tint = Color.Unspecified
        )
    }
}