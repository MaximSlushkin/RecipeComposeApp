package com.yourcompany.recipecomposeapp.ingredients

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.yourcompany.recipecomposeapp.R

@Composable
fun InstructionItem(
    step: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.mainPadding),
                vertical = dimensionResource(R.dimen.cardPadding)
            )
    ) {
        Text(
            text = step,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(
                vertical = dimensionResource(R.dimen.cardPadding)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InstructionItemPreview() {
    InstructionItem(
        step = "1. Сформируйте котлеты из фарша и обжарьте на сковороде до золотистой корочки"
    )
}