package com.yourcompany.recipecomposeapp.fixtures

import com.yourcompany.recipecomposeapp.data.model.CategoryDto

object CategoryTestFixtures {

    /**
     * Создает CategoryDto с значениями по умолчанию
     * @param id ID категории (по умолчанию 1)
     * @param title Название категории (по умолчанию "Test Category")
     * @param description Описание категории (по умолчанию "Test Description")
     * @param imageUrl URL изображения (по умолчанию "category.jpg")
     */
    fun createCategoryDto(
        id: Int = 1,
        title: String = "Test Category",
        description: String = "Test Description",
        imageUrl: String = "category.jpg"
    ) = CategoryDto(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl
    )

    /**
     * Создает список CategoryDto
     * @param count Количество категорий
     * @param baseId Базовый ID для генерации
     */
    fun createCategoryDtoList(
        count: Int = 3,
        baseId: Int = 1
    ) = List(count) { index ->
        createCategoryDto(
            id = baseId + index,
            title = "Test Category ${baseId + index}",
            description = "Description for category ${baseId + index}"
        )
    }

    /**
     * Создает CategoryDto с длинным описанием
     */
    fun createCategoryWithLongDescription(
        id: Int = 1,
        title: String = "Category with Long Description"
    ): CategoryDto {
        val longDescription =
            "A".repeat(1000) + " Long description with many characters " + "B".repeat(1000)
        return createCategoryDto(
            id = id,
            title = title,
            description = longDescription
        )
    }

    /**
     * Создает CategoryDto с пустым названием
     */
    fun createCategoryWithEmptyTitle(
        id: Int = 1,
        description: String = "Category with empty title"
    ) = createCategoryDto(
        id = id,
        title = "",
        description = description
    )
}