package com.yourcompany.recipecomposeapp.data.repository

import com.yourcompany.recipecomposeapp.core.network.api.RecipesApiService
import com.yourcompany.recipecomposeapp.data.database.RecipesDatabase
import com.yourcompany.recipecomposeapp.data.database.dao.CategoryDao
import com.yourcompany.recipecomposeapp.data.database.dao.RecipeDao
import com.yourcompany.recipecomposeapp.data.database.entity.CategoryEntity
import com.yourcompany.recipecomposeapp.data.database.entity.RecipeEntity
import com.yourcompany.recipecomposeapp.data.model.toDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

class RecipesRepositoryTest {

    private val apiService = mockk<RecipesApiService>()
    private val database = mockk<RecipesDatabase>(relaxed = true)
    private val categoryDao = mockk<CategoryDao>()
    private val recipeDao = mockk<RecipeDao>()

    private lateinit var repository: RecipesRepositoryImpl

    @Before
    fun setup() {
        every { database.categoryDao() } returns categoryDao
        every { database.recipeDao() } returns recipeDao
        repository = RecipesRepositoryImpl(apiService, database)
    }

    @After
    fun tearDown() {
        io.mockk.clearAllMocks()
    }

    @Test
    fun `getCategories emits categories from database`() = runTest {
        val categoryEntities = listOf(
            CategoryEntity(1, "Breakfast", "Morning meals", "breakfast.jpg"),
            CategoryEntity(2, "Lunch", "Midday meals", "lunch.jpg")
        )
        val expectedCategories = categoryEntities.map { it.toDto() }

        coEvery { categoryDao.getAllCategories() } returns flowOf(categoryEntities)
        coEvery { apiService.getCategories() } returns expectedCategories

        val result = repository.getCategories().first()

        assertEquals(2, result.size)
        assertEquals("Breakfast", result[0].title)
        assertEquals("Lunch", result[1].title)
        coVerify { categoryDao.getAllCategories() }
    }

    @Test
    fun `getCategories still emits data when api throws exception`() = runTest {

        val categoryEntities = listOf(
            CategoryEntity(1, "Breakfast", "Morning meals", "breakfast.jpg")
        )

        coEvery { categoryDao.getAllCategories() } returns flowOf(categoryEntities)
        coEvery { apiService.getCategories() } throws IOException("Network error")

        val result = repository.getCategories().first()

        assertEquals(1, result.size)
        assertEquals("Breakfast", result[0].title)
        coVerify { apiService.getCategories() }
        coVerify { categoryDao.getAllCategories() }
    }

    @Test
    fun `getRecipesByCategory returns flow filtered by categoryId`() = runTest {

        val categoryId = 5

        val recipeEntities = listOf(
            RecipeEntity(1, "Recipe 1", categoryId, "img1.jpg", "[]", "[]", 4),
            RecipeEntity(2, "Recipe 2", categoryId, "img2.jpg", "[]", "[]", 4)
        )

        coEvery { recipeDao.getRecipesByCategory(categoryId) } returns flowOf(recipeEntities)
        coEvery { apiService.getRecipesByCategory(categoryId) } returns
                recipeEntities.map { it.toDto() }

        val result = repository.getRecipesByCategory(categoryId).first()

        assertEquals(2, result.size)
        result.forEach { recipeDto ->
            assertEquals(categoryId, recipeDto.categoryIds.first())
        }
        coVerify { recipeDao.getRecipesByCategory(categoryId) }
    }
}