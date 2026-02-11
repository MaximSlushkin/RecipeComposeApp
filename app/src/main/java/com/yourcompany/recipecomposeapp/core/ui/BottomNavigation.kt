package com.yourcompany.recipecomposeapp.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yourcompany.recipecomposeapp.R
import com.yourcompany.recipecomposeapp.core.navigation.Destination
import com.yourcompany.recipecomposeapp.ui.theme.RecipesAppTheme
import com.yourcompany.recipecomposeapp.core.utils.FavoriteDataStoreManager

@Composable
fun BottomNavigation(
    navController: NavController,
    onCategoriesClick: () -> Unit,
    onFavoritesClick: () -> Unit,
) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry.value?.destination
    val currentRoute = currentDestination?.route

    val context = LocalContext.current
    val favoriteManager = remember { FavoriteDataStoreManager(context) }

    val favoriteCount by favoriteManager.getFavoriteCountFlow()
        .collectAsState(initial = 0)

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
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentColor = if (currentRoute == Destination.Categories.route) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            ) {
                Text(
                    text = "Категории".uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.surface
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
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentColor = if (currentRoute == Destination.Favorites.route) {
                        MaterialTheme.colorScheme.onSecondary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Избранное".uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.surface
                    )

                    BadgedBox(
                        badge = {
                            if (favoriteCount > 0) {
                                Badge {
                                    Text(
                                        text = favoriteCount.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.surface
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_heart_empty),
                            contentDescription = "Избранное",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationPreview() {
    RecipesAppTheme {
        val navController = rememberNavController()
        BottomNavigation(
            navController = navController,
            onCategoriesClick = { },
            onFavoritesClick = { }
        )
    }
}