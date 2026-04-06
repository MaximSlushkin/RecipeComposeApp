package com.yourcompany.recipecomposeapp.features.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class RecipesComposeScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : ComposeScreen<RecipesComposeScreen>(
    semanticsProvider = semanticsProvider,
    viewBuilderAction = { hasTestTag("recipes_screen") }
) {
    val loadingIndicator: KNode = child { hasTestTag("loading_indicator") }
    val emptyState: KNode = child { hasTestTag("empty_state") }
    val errorMessage: KNode = child { hasTestTag("error_message") }
    val retryButton: KNode = child { hasText("Повторить") }
    val recipesList: KNode = child { hasTestTag("recipes_list") }
    val recipeItem: KNode = child { hasTestTag("recipe_item") }
}