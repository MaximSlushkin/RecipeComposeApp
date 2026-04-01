package com.yourcompany.recipecomposeapp.features.recipes.presentation

import androidx.lifecycle.SavedStateHandle
import com.yourcompany.recipecomposeapp.data.repository.RecipesRepository
import com.yourcompany.recipecomposeapp.features.recipes.presentation.model.toUiModel
import com.yourcompany.recipecomposeapp.fixtures.RecipeTestFixtures
import com.yourcompany.recipecomposeapp.core.utils.Constants
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalCoroutinesApi::class)
class RecipesViewModelTest {

    private val repository: RecipesRepository = mockk()
    private lateinit var viewModel: RecipesViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    private fun createViewModel(
        categoryId: Int = 1,
        categoryTitle: String = "Test Category",
        categoryImageUrl: String = "test.jpg"
    ): RecipesViewModel {
        val savedStateHandle = SavedStateHandle().apply {
            set(Constants.KEY_CATEGORY_ID, categoryId)
            set(
                Constants.KEY_CATEGORY_TITLE,
                URLEncoder.encode(categoryTitle, StandardCharsets.UTF_8.toString())
            )
            set(
                Constants.KEY_CATEGORY_IMAGE_URL,
                URLEncoder.encode(categoryImageUrl, StandardCharsets.UTF_8.toString())
            )
        }

        return RecipesViewModel(savedStateHandle, repository)
    }

    @Test
    fun `loads recipes for category - проверка загрузки рецептов для категории`() = runTest {
        val categoryId = 5
        val categoryTitle = "Бургеры"
        val categoryImageUrl = "burgers.jpg"

        val recipeDtos = RecipeTestFixtures.createRecipeDtoList(count = 3, baseId = 1)
        val expectedRecipes = recipeDtos.map { it.toUiModel() }

        coEvery { repository.getRecipesByCategory(categoryId) } returns flowOf(recipeDtos)

        viewModel = createViewModel(
            categoryId = categoryId,
            categoryTitle = categoryTitle,
            categoryImageUrl = categoryImageUrl
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals(categoryTitle, state.categoryTitle)
        assertTrue(state.categoryImageUrl.isNotEmpty())
        assertEquals(3, state.recipes.size)
        assertEquals(expectedRecipes[0].id, state.recipes[0].id)
        assertEquals(expectedRecipes[0].title, state.recipes[0].title)

        coVerify(exactly = 1) { repository.getRecipesByCategory(categoryId) }
    }

    @Test
    fun `loads recipes for category with default values - проверка с параметрами по умолчанию`() =
        runTest {
            val categoryId = -1

            val recipeDtos = RecipeTestFixtures.createRecipeDtoList(count = 2)
            coEvery { repository.getRecipesByCategory(categoryId) } returns flowOf(recipeDtos)

            viewModel = createViewModel(
                categoryId = categoryId,
                categoryTitle = "",
                categoryImageUrl = ""
            )

            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(Constants.DEFAULT_CATEGORY_TITLE, state.categoryTitle)
            assertEquals(2, state.recipes.size)
        }

    @Test
    fun `shows error when repository throws - проверка обработки ошибки загрузки`() = runTest {
        val categoryId = 1
        val errorMessage = "Failed to load recipes"

        coEvery { repository.getRecipesByCategory(categoryId) } returns flow {
            throw IOException(errorMessage)
        }

        viewModel = createViewModel(categoryId = categoryId)
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertNotNull(state.errorMessage)
        assertTrue(state.errorMessage?.contains(errorMessage) == true)
        assertTrue(state.recipes.isEmpty())

        coVerify(exactly = 1) { repository.getRecipesByCategory(categoryId) }
    }

    @Test
    fun `shows loading state during network call - проверка промежуточного состояния`() = runTest {
        val categoryId = 1
        val recipeDtos = RecipeTestFixtures.createRecipeDtoList(count = 2)

        coEvery { repository.getRecipesByCategory(categoryId) } returns flow {
            kotlinx.coroutines.delay(100)
            emit(recipeDtos)
        }

        viewModel = createViewModel(categoryId = categoryId)

        val initialState = viewModel.uiState.value
        assertTrue("Начальное состояние должно быть loading", initialState.isLoading)
        assertNull(initialState.errorMessage)

        advanceUntilIdle()

        val finalState = viewModel.uiState.value
        assertFalse(finalState.isLoading)
        assertEquals(2, finalState.recipes.size)
    }

    @Test
    fun `retry - повторная загрузка после ошибки`() = runTest {
        val categoryId = 1

        coEvery { repository.getRecipesByCategory(categoryId) } returns flow {
            throw IOException("Network error")
        }

        viewModel = createViewModel(categoryId = categoryId)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorMessage)

        val recipeDtos = RecipeTestFixtures.createRecipeDtoList(count = 3)
        coEvery { repository.getRecipesByCategory(categoryId) } returns flowOf(recipeDtos)

        viewModel.retry()
        advanceUntilIdle()

        val successState = viewModel.uiState.value
        assertFalse(successState.isLoading)
        assertNull(successState.errorMessage)
        assertEquals(3, successState.recipes.size)

        coVerify(exactly = 2) { repository.getRecipesByCategory(categoryId) }
    }

    @Test
    fun `refreshRecipes - ручное обновление рецептов`() = runTest {
        val categoryId = 1

        val initialRecipes = RecipeTestFixtures.createRecipeDtoList(count = 1)
        coEvery { repository.getRecipesByCategory(categoryId) } returns flowOf(initialRecipes)

        viewModel = createViewModel(categoryId = categoryId)
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.recipes.size)

        val updatedRecipes = RecipeTestFixtures.createRecipeDtoList(count = 4)

        coEvery { repository.refreshRecipes(categoryId) } coAnswers {
            coEvery { repository.getRecipesByCategory(categoryId) } returns flowOf(updatedRecipes)
            Unit
        }

        viewModel.refreshRecipes()

        viewModel.retry()

        advanceUntilIdle()

        val finalState = viewModel.uiState.value
        assertFalse(finalState.isLoading)
        assertEquals(4, finalState.recipes.size)

        coVerify(exactly = 1) { repository.refreshRecipes(categoryId) }
    }

    @Test
    fun `handles empty recipe list correctly - проверка пустого списка рецептов`() = runTest {
        val categoryId = 1

        coEvery { repository.getRecipesByCategory(categoryId) } returns flowOf(emptyList())

        viewModel = createViewModel(categoryId = categoryId)
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertTrue(state.recipes.isEmpty())
        assertTrue(state.isEmpty)
        assertFalse(state.hasError)
    }

    @Test
    fun `handles URL encoded parameters correctly - проверка декодирования параметров`() = runTest {
        val categoryId = 1
        val encodedTitle =
            URLEncoder.encode("Бургеры & Сэндвичи", StandardCharsets.UTF_8.toString())
        val encodedImageUrl = URLEncoder.encode(
            "https://example.com/image with spaces.jpg",
            StandardCharsets.UTF_8.toString()
        )

        val savedStateHandle = SavedStateHandle().apply {
            set(Constants.KEY_CATEGORY_ID, categoryId)
            set(Constants.KEY_CATEGORY_TITLE, encodedTitle)
            set(Constants.KEY_CATEGORY_IMAGE_URL, encodedImageUrl)
        }

        coEvery { repository.getRecipesByCategory(categoryId) } returns flowOf(emptyList())

        viewModel = RecipesViewModel(savedStateHandle, repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals("Бургеры & Сэндвичи", state.categoryTitle)
        assertEquals("https://example.com/image with spaces.jpg", state.categoryImageUrl)
    }

    @Test
    fun `handles malformed URL encoded parameters gracefully - проверка обработки некорректного URL`() =
        runTest {
            val categoryId = 1
            val malformedTitle = "%E0%A4%A"

            val savedStateHandle = SavedStateHandle().apply {
                set(Constants.KEY_CATEGORY_ID, categoryId)
                set(Constants.KEY_CATEGORY_TITLE, malformedTitle)
            }

            coEvery { repository.getRecipesByCategory(categoryId) } returns flowOf(emptyList())

            viewModel = RecipesViewModel(savedStateHandle, repository)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(Constants.DEFAULT_CATEGORY_TITLE, state.categoryTitle)
        }
}