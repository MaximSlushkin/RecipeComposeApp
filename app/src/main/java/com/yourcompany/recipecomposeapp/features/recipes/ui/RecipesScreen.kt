package com.yourcompany.recipecomposeapp.features.recipes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourcompany.recipecomposeapp.R
import com.yourcompany.recipecomposeapp.core.ui.ScreenHeader
import com.yourcompany.recipecomposeapp.core.ui.components.ingredients.presentation.model.IngredientUiModel
import com.yourcompany.recipecomposeapp.features.recipes.presentation.RecipesViewModel
import com.yourcompany.recipecomposeapp.features.recipes.presentation.model.RecipeUiModel
import com.yourcompany.recipecomposeapp.features.recipes.presentation.model.RecipesUiState
import com.yourcompany.recipecomposeapp.ui.theme.RecipesAppTheme

@Composable
fun RecipesScreen(
    viewModel: RecipesViewModel,
    modifier: Modifier = Modifier,
    onRecipeClick: (Int) -> Unit = { }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RecipesContent(
        uiState = uiState,
        onRecipeClick = onRecipeClick,
        onRetry = { viewModel.retry() },
        modifier = modifier
    )
}

/**
 * Stateless composable для тестирования UI экрана рецептов
 * @param uiState Состояние UI для отображения
 * @param onRecipeClick Callback при клике на рецепт
 * @param onRetry Callback при повторной попытке загрузки
 * @param modifier Модификатор
 */
@Composable
fun RecipesContent(
    uiState: RecipesUiState,
    onRecipeClick: (Int) -> Unit,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("recipes_screen")
    ) {
        ScreenHeader(
            header = uiState.categoryTitle,
            imageUrl = uiState.categoryImageUrl,
            imageRes = R.drawable.bcg_categories,
            modifier = Modifier,
        )

        when {
            uiState.isLoading -> {
                LoadingState()
            }

            uiState.hasError -> {
                ErrorState(
                    errorMessage = uiState.errorMessage ?: "Неизвестная ошибка",
                    onRetry = onRetry
                )
            }

            uiState.isEmpty -> {
                EmptyState()
            }

            else -> {
                RecipesList(
                    recipes = uiState.recipes,
                    onRecipeClick = onRecipeClick
                )
            }
        }
    }
}

@Composable
private fun RecipesList(
    recipes: List<RecipeUiModel>,
    onRecipeClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("recipes_list"),
        contentPadding = PaddingValues(
            vertical = dimensionResource(R.dimen.mainPadding)
        ),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.cardPadding)
        )
    ) {
        items(
            items = recipes,
            key = { it.id }
        ) { recipe ->
            RecipeItem(
                recipe = recipe,
                onClick = { recipeId ->
                    onRecipeClick(recipeId)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.mainPadding))
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.testTag("loading_indicator")
            )
            Text(
                text = "Загрузка рецептов...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun ErrorState(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("error_message")
            )
            Button(onClick = onRetry) {
                Text("Повторить")
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Рецепты для этой категории скоро появятся",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(16.dp)
                .testTag("empty_state")
        )
    }
}

@Preview(showBackground = true, name = "Recipes Screen - Loading State")
@Composable
fun RecipesScreenLoadingPreview() {
    RecipesAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RecipesContent(
                uiState = RecipesUiState(
                    isLoading = true,
                    categoryTitle = "Бургеры"
                ),
                onRecipeClick = { }
            )
        }
    }
}

@Preview(showBackground = true, name = "Recipes Screen - Error State")
@Composable
fun RecipesScreenErrorPreview() {
    RecipesAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RecipesContent(
                uiState = RecipesUiState(
                    errorMessage = "Не удалось загрузить рецепты. Проверьте подключение к интернету.",
                    isLoading = false,
                    categoryTitle = "Бургеры"
                ),
                onRecipeClick = { },
                onRetry = { }
            )
        }
    }
}

@Preview(showBackground = true, name = "Recipes Screen - Empty State")
@Composable
fun RecipesScreenEmptyPreview() {
    RecipesAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RecipesContent(
                uiState = RecipesUiState(
                    recipes = emptyList(),
                    isLoading = false,
                    categoryTitle = "Бургеры"
                ),
                onRecipeClick = { }
            )
        }
    }
}

@Preview(showBackground = true, name = "Recipes Screen - Success State")
@Composable
fun RecipesScreenSuccessPreview() {
    RecipesAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val mockRecipes = listOf(
                RecipeUiModel(
                    id = 1,
                    title = "Классический бургер",
                    imageUrl = "",
                    ingredients = listOf(
                        IngredientUiModel("Говяжий фарш", "500 г"),
                        IngredientUiModel("Булочка для бургера", "2 шт"),
                        IngredientUiModel("Сыр Чеддер", "200 г"),
                        IngredientUiModel("Помидор", "1 шт"),
                        IngredientUiModel("Салат Айсберг", "50 г"),
                        IngredientUiModel("Огурцы маринованные", "4 шт"),
                        IngredientUiModel("Кетчуп и горчица", "по вкусу")
                    ),
                    method = listOf(
                        "1. Сформируйте котлеты из фарша, посолите и поперчите",
                        "2. Обжарьте котлеты на сковороде до золотистой корочки по 3-4 минуты с каждой стороны",
                        "3. Поджарьте булочки на гриле или сухой сковороде",
                        "4. Соберите бургер: нижняя булочка, котлета, сыр, овощи, соус, верхняя булочка"
                    ),
                    servings = 4
                ),
                RecipeUiModel(
                    id = 2,
                    title = "Чизбургер",
                    imageUrl = "",
                    ingredients = listOf(
                        IngredientUiModel("Говяжий фарш", "500 г"),
                        IngredientUiModel("Булочка для бургера", "2 шт"),
                        IngredientUiModel("Сыр Чеддер", "4 ломтика"),
                        IngredientUiModel("Лук репчатый", "1 шт"),
                        IngredientUiModel("Соус для бургера", "4 ст.л.")
                    ),
                    method = listOf(
                        "1. Приготовьте котлеты из фарша",
                        "2. Обжарьте лук до золотистого цвета",
                        "3. Соберите бургер с сыром и луком"
                    ),
                    servings = 2
                ),
                RecipeUiModel(
                    id = 3,
                    title = "Вегетарианский бургер",
                    imageUrl = "",
                    ingredients = listOf(
                        IngredientUiModel("Котлета из нута", "4 шт"),
                        IngredientUiModel("Булочка с кунжутом", "4 шт"),
                        IngredientUiModel("Авокадо", "1 шт"),
                        IngredientUiModel("Помидоры черри", "8 шт"),
                        IngredientUiModel("Руккола", "50 г")
                    ),
                    method = listOf(
                        "1. Разогрейте котлеты на сковороде",
                        "2. Нарежьте авокадо и помидоры",
                        "3. Соберите бургер с зеленью и овощами"
                    ),
                    servings = 4
                )
            )

            RecipesContent(
                uiState = RecipesUiState(
                    recipes = mockRecipes,
                    isLoading = false,
                    categoryTitle = "БУРГЕРЫ",
                    categoryImageUrl = "https://example.com/burgers.jpg"
                ),
                onRecipeClick = { recipeId -> }
            )
        }
    }
}

@Preview(showBackground = true, name = "Recipes Screen - Long List")
@Composable
fun RecipesScreenLongListPreview() {
    RecipesAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val longRecipesList = (1..20).map { index ->
                RecipeUiModel(
                    id = index,
                    title = "Рецепт №$index",
                    imageUrl = "",
                    ingredients = emptyList(),
                    method = emptyList(),
                    servings = 2
                )
            }

            RecipesContent(
                uiState = RecipesUiState(
                    recipes = longRecipesList,
                    isLoading = false,
                    categoryTitle = "ПОПУЛЯРНЫЕ РЕЦЕПТЫ"
                ),
                onRecipeClick = { }
            )
        }
    }
}

@Preview(showBackground = true, name = "Recipes Screen - With Long Category Title")
@Composable
fun RecipesScreenLongTitlePreview() {
    RecipesAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val mockRecipes = listOf(
                RecipeUiModel(
                    id = 1,
                    title = "Тестовый рецепт",
                    imageUrl = "",
                    ingredients = emptyList(),
                    method = emptyList(),
                    servings = 1
                )
            )

            RecipesContent(
                uiState = RecipesUiState(
                    recipes = mockRecipes,
                    isLoading = false,
                    categoryTitle = "Очень длинное название категории, которое может не поместиться в одну строку"
                ),
                onRecipeClick = { }
            )
        }
    }
}