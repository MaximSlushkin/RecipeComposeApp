package com.yourcompany.recipecomposeapp.features.data.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yourcompany.recipecomposeapp.data.database.RecipesDatabase
import com.yourcompany.recipecomposeapp.data.database.entity.CategoryEntity
import com.yourcompany.recipecomposeapp.data.database.entity.RecipeEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RecipesDaoTest {

    private lateinit var database: RecipesDatabase
    private lateinit var categoryDao: com.yourcompany.recipecomposeapp.data.database.dao.CategoryDao
    private lateinit var recipeDao: com.yourcompany.recipecomposeapp.data.database.dao.RecipeDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RecipesDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        categoryDao = database.categoryDao()
        recipeDao = database.recipeDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertsAndRetrievesCategories() = runTest {
        val categories = listOf(
            CategoryEntity(1, "Завтраки", "Лёгкие завтраки", "breakfast.jpg"),
            CategoryEntity(2, "Обеды", "Сытные обеды", "lunch.jpg")
        )

        categoryDao.insertCategories(categories)
        val retrieved = categoryDao.getAllCategories().first()

        assertEquals(2, retrieved.size)
    }

    @Test
    fun insertReplacesDuplicateCategory() = runTest {
        val originalCategory =
            CategoryEntity(1, "Завтраки", "Оригинальное описание", "breakfast.jpg")
        categoryDao.insertCategory(originalCategory)

        val updatedCategory =
            CategoryEntity(1, "Завтраки и бранчи", "Обновленное описание", "updated_breakfast.jpg")
        categoryDao.insertCategory(updatedCategory)

        val allCategories = categoryDao.getAllCategories().first()
        assertEquals(1, allCategories.size)

        val retrieved = categoryDao.getCategoryById(1)
        assertEquals("Завтраки и бранчи", retrieved?.name)
    }

    @Test
    fun getRecipesByCategoryReturnsCorrectItems() = runTest {
        val category1 = CategoryEntity(1, "Завтраки", "Описание", "img1.jpg")
        val category2 = CategoryEntity(2, "Обеды", "Описание", "img2.jpg")
        categoryDao.insertCategory(category1)
        categoryDao.insertCategory(category2)

        val recipes = listOf(
            RecipeEntity(1, "Овсяная каша", 1, "oatmeal.jpg", "[]", "[]", 2),
            RecipeEntity(2, "Яичница", 1, "eggs.jpg", "[]", "[]", 1),
            RecipeEntity(3, "Борщ", 2, "borscht.jpg", "[]", "[]", 4)
        )

        recipeDao.insertRecipes(recipes)
        val recipesForCategory1 = recipeDao.getRecipesByCategory(1).first()

        assertEquals(2, recipesForCategory1.size)
        assertTrue(recipesForCategory1.all { it.categoryId == 1 })
    }

    @Test
    fun emptyDatabaseReturnsEmptyList() = runTest {
        val categories = categoryDao.getAllCategories().first()
        val recipes = recipeDao.getRecipesByCategory(1).first()

        assertTrue(categories.isEmpty())
        assertTrue(recipes.isEmpty())
        assertEquals(0, categoryDao.getCategoriesCount())
    }

    @Test
    fun getCategoryByIdReturnsCorrectCategory() = runTest {
        val category = CategoryEntity(42, "Десерты", "Сладкие десерты", "desserts.jpg")
        categoryDao.insertCategory(category)

        val retrieved = categoryDao.getCategoryById(42)

        assertEquals(42, retrieved?.id)
        assertEquals("Десерты", retrieved?.name)
    }

    @Test
    fun deleteAllCategoriesClearsTable() = runTest {
        val categories = listOf(
            CategoryEntity(1, "Категория 1", "Описание 1", "img1.jpg"),
            CategoryEntity(2, "Категория 2", "Описание 2", "img2.jpg")
        )
        categoryDao.insertCategories(categories)
        assertEquals(2, categoryDao.getCategoriesCount())

        categoryDao.deleteAllCategories()

        assertEquals(0, categoryDao.getCategoriesCount())
        assertTrue(categoryDao.getAllCategories().first().isEmpty())
    }

    @Test
    fun getRecipeByIdReturnsFlowWithUpdates() = runTest {
        val category = CategoryEntity(1, "Завтраки", "Описание", "img.jpg")
        categoryDao.insertCategory(category)

        val recipe = RecipeEntity(100, "Первоначальное название", 1, "recipe.jpg", "[]", "[]", 2)
        recipeDao.insertRecipe(recipe)

        val updatedRecipe = recipe.copy(title = "Обновленное название")
        recipeDao.insertRecipe(updatedRecipe)

        val result = recipeDao.getRecipeById(100).first()
        assertEquals("Обновленное название", result?.title)
    }
}