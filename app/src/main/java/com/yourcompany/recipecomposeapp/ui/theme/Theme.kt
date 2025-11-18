package com.yourcompany.recipecomposeapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val RecipesAppLightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = AccentColor,
    tertiary = PrimaryColor,
    tertiaryContainer = SliderTrackColor,
    background = BackgroundColor,
    surface = SurfaceColor,
    surfaceVariant = SurfaceVariantColor,
    outlineVariant = DividerColor,
    onPrimary = TextPrimaryColor,
    onSecondary = TextSecondaryColor,
    onSurface = HeadersColor,
    onSurfaceVariant = TextSecondaryColor,
)

private val RecipesAppDarkColorScheme = darkColorScheme(
    primary = PrimaryColorDark,
    secondary = AccentColorDark,
    tertiary = PrimaryColorDark,
    tertiaryContainer = SliderTrackColorDark,
    outline = SliderTrackColorDark,
    background = BackgroundColorDark,
    surface = SurfaceColorDark,
    onPrimary = TextPrimaryColorDark,
    onSecondary = TextSecondaryColorDark,
    onSurface = HeadersColorDark,
    onSurfaceVariant = TextSecondaryColorDark,
)

@Composable
fun RecipesAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        RecipesAppDarkColorScheme
    } else {
        RecipesAppLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = recipesAppTypography,
        content = content
    )
}