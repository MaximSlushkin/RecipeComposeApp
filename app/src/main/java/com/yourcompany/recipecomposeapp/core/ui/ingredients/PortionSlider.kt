package com.yourcompany.recipecomposeapp.core.ui.ingredients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.yourcompany.recipecomposeapp.R

@Composable
fun PortionsSlider(
    currentPortions: Int,
    onPortionsChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minPortions: Int = 1,
    maxPortions: Int = 12
) {
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
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Slider(
            value = currentPortions.toFloat(),
            onValueChange = { newValue -> onPortionsChanged(newValue.toInt()) },
            valueRange = minPortions.toFloat()..maxPortions.toFloat(),
            steps = maxPortions - minPortions - 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(R.dimen.cardPadding))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PortionsSliderPreview() {
    MaterialTheme {
        PortionsSlider(
            currentPortions = 1,
            onPortionsChanged = { }
        )
    }
}