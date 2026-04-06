package com.yourcompany.recipecomposeapp.features.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CategoriesComposeScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : ComposeScreen<CategoriesComposeScreen>(
    semanticsProvider = semanticsProvider,
    viewBuilderAction = { hasTestTag("categories_screen") }
) {
    val loadingIndicator: KNode = child { hasTestTag("loading_indicator") }
    val categoriesGrid: KNode = child { hasTestTag("categories_grid") }
    val categoryItem: KNode = child { hasTestTag("category_item") }
    val errorMessage: KNode = child { hasTestTag("error_message") }
    val emptyState: KNode = child { hasTestTag("empty_state") }
    val retryButton: KNode = child { hasText("Повторить") }
}