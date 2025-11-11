package com.yourcompany.recipecomposeapp.core.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.yourcompany.recipecomposeapp.core.ui.navigation.Destination
import com.yourcompany.recipecomposeapp.R
import com.yourcompany.recipecomposeapp.ui.theme.AccentColor
import com.yourcompany.recipecomposeapp.ui.theme.PrimaryColor

@Composable
fun BottomNavigation(
    navController: NavController,
    onCategoriesClick: () -> Unit,
    onFavoritesClick: () -> Unit,
) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry.value?.destination
    val currentRoute = currentDestination?.route

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 8.dp,
                bottom = dimensionResource(R.dimen.mainPadding),
                start = dimensionResource(R.dimen.mainPadding),
                end = dimensionResource(R.dimen.mainPadding)
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onCategoriesClick,
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp),
                shape = RoundedCornerShape(dimensionResource(R.dimen.basicCornerRadius)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentRoute == Destination.Categories.route) {
                        PrimaryColor
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Text(
                    text = "Категории".uppercase(),
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Button(
                onClick = onFavoritesClick,
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .padding(start = 4.dp),
                shape = RoundedCornerShape(dimensionResource(R.dimen.basicCornerRadius)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentRoute == Destination.Favorites.route) {
                        AccentColor
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Избранное".uppercase(),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_heart_empty),
                        contentDescription = "Избранное",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}