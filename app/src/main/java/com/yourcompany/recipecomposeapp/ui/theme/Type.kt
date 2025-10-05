package com.yourcompany.recipecomposeapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.yourcompany.recipecomposeapp.montserratAlternatesFontFamily
import com.yourcompany.recipecomposeapp.montserratFontFamily

val Typography = Typography(

    displayLarge = TextStyle(
        fontFamily = montserratAlternatesFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
    ),

    titleMedium = TextStyle(
        fontFamily = montserratAlternatesFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
    ),

    bodyMedium = TextStyle(
        fontFamily = montserratFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ),

    bodySmall = TextStyle(
        fontFamily = montserratFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
    ),

    labelLarge = TextStyle(
        fontFamily = montserratFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
)