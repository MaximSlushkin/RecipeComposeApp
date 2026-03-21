package com.yourcompany.recipecomposeapp.fixtures

import com.yourcompany.recipecomposeapp.data.model.IngredientDto
import com.yourcompany.recipecomposeapp.data.model.RecipeDto

object RecipeTestFixtures {

    /**
     * Создает RecipeDto с значениями по умолчанию
     * @param id ID рецепта (по умолчанию 1)
     * @param title Название рецепта (по умолчанию "Test Recipe")
     * @param ingredients Список ингредиентов (по умолчанию базовый список)
     * @param method Шаги приготовления (по умолчанию базовый список)
     * @param imageUrl URL изображения (по умолчанию "recipe.jpg")
     * @param categoryIds ID категорий (по умолчанию [1])
     * @param servings Количество порций (по умолчанию 4)
     */
    fun createRecipeDto(
        id: Int = 1,
        title: String = "Test Recipe",
        ingredients: List<IngredientDto> = listOf(
            IngredientDto("500", "г", "Говяжий фарш"),
            IngredientDto("2", "шт", "Луковица")
        ),
        method: List<String> = listOf(
            "Step 1: Prepare ingredients",
            "Step 2: Cook"
        ),
        imageUrl: String = "recipe.jpg",
        categoryIds: List<Int> = listOf(1),
        servings: Int = 4
    ) = RecipeDto(
        id = id,
        title = title,
        ingredients = ingredients,
        method = method,
        imageUrl = imageUrl,
        categoryIds = categoryIds,
        servings = servings
    )

    /**
     * Создает список RecipeDto
     * @param count Количество рецептов в списке
     * @param baseId Базовый ID для генерации (будет увеличиваться на 1)
     */
    fun createRecipeDtoList(
        count: Int = 3,
        baseId: Int = 1
    ) = List(count) { index ->
        createRecipeDto(
            id = baseId + index,
            title = "Test Recipe ${baseId + index}"
        )
    }

    /**
     * Создает RecipeDto с минимальными данными для тестирования граничных случаев
     */
    fun createMinimalRecipeDto(
        id: Int = 1,
        title: String = "Minimal Recipe"
    ) = RecipeDto(
        id = id,
        title = title,
        ingredients = emptyList(),
        method = emptyList(),
        imageUrl = "",
        categoryIds = emptyList(),
        servings = 1
    )
}