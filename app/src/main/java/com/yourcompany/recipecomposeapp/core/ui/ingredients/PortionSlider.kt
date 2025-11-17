package com.yourcompany.recipecomposeapp.core.ui.ingredients

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourcompany.recipecomposeapp.R
import com.yourcompany.recipecomposeapp.ui.theme.RecipesAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortionsSlider(
    currentPortions: Int,
    onPortionsChanged: (Int) -> Unit,
    defaultPortions: Int = 4,
    modifier: Modifier = Modifier,
    minPortions: Int = 1,
    maxPortions: Int = 8
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.mainPadding))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Порции: $currentPortions",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Slider(
            value = currentPortions.toFloat(),
            onValueChange = { newValue -> onPortionsChanged(newValue.toInt()) },
            valueRange = minPortions.toFloat()..maxPortions.toFloat(),
            steps = maxPortions - minPortions - 1,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.tertiary,
                inactiveTrackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f)
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(width = 8.dp, height = 30.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                )
            },
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(R.dimen.cardPadding))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PortionsSliderPreview() {
    RecipesAppTheme {
        PortionsSlider(
            currentPortions = 4,
            onPortionsChanged = { }
        )
    }
}