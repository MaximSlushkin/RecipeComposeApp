package com.yourcompany.recipecomposeapp.features.categories.presentation

import app.cash.turbine.test
import com.yourcompany.recipecomposeapp.data.repository.RecipesRepository
import com.yourcompany.recipecomposeapp.features.categories.presentation.model.toUiModel
import com.yourcompany.recipecomposeapp.fixtures.CategoryTestFixtures
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

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModelTest {

    private val repository: RecipesRepository = mockk()
    private lateinit var viewModel: CategoriesViewModel
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

    @Test
    fun `loads categories from repository - проверка успешной загрузки категорий`() = runTest {
        val categoryDtos = CategoryTestFixtures.createCategoryDtoList(count = 3, baseId = 1)
        val expectedCategories = categoryDtos.map { it.toUiModel() }

        coEvery { repository.getCategories() } returns flowOf(categoryDtos)

        viewModel = CategoriesViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(3, state.categories.size)
        assertEquals(expectedCategories[0].id, state.categories[0].id)
        assertEquals(expectedCategories[0].title, state.categories[0].title)
        assertEquals(expectedCategories[0].description, state.categories[0].description)
        assertFalse(state.isEmpty)

        coVerify(exactly = 1) { repository.getCategories() }
    }

    @Test
    fun `loads categories from repository - проверка с пустым списком`() = runTest {
        val emptyCategoryDtos = emptyList<com.yourcompany.recipecomposeapp.data.model.CategoryDto>()

        coEvery { repository.getCategories() } returns flowOf(emptyCategoryDtos)

        viewModel = CategoriesViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertNull(state.error)
        assertTrue(state.categories.isEmpty())
        assertTrue(state.isEmpty)

        coVerify(exactly = 1) { repository.getCategories() }
    }

    @Test
    fun `shows error when repository throws - проверка обработки ошибки`() = runTest {
        val errorMessage = "Network error"
        coEvery { repository.getCategories() } returns flow {
            throw IOException(errorMessage)
        }

        viewModel = CategoriesViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertTrue(state.error?.contains(errorMessage) == true)
        assertTrue(state.isEmpty)
        assertTrue(state.categories.isEmpty())

        coVerify(exactly = 1) { repository.getCategories() }
    }

    @Test
    fun `shows error when repository throws - проверка с разными типами ошибок`() = runTest {
        val testCases = listOf(
            "Runtime exception" to RuntimeException("Unexpected error"),
            "Null pointer" to NullPointerException("Null value")
        )

        testCases.forEach { (description, exception) ->
            clearAllMocks()

            coEvery { repository.getCategories() } returns flow {
                throw exception
            }

            viewModel = CategoriesViewModel(repository)
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertFalse(state.isLoading)
            assertNotNull(state.error)
            assertTrue(state.categories.isEmpty())

            coVerify(exactly = 1) { repository.getCategories() }
        }
    }

    @Test
    fun `retry - повторная загрузка после ошибки`() = runTest {
        coEvery { repository.getCategories() } returns flow {
            throw IOException("Network error")
        }

        viewModel = CategoriesViewModel(repository)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)

        val categoryDtos = CategoryTestFixtures.createCategoryDtoList(count = 2)
        coEvery { repository.getCategories() } returns flowOf(categoryDtos)

        viewModel.retry()
        advanceUntilIdle()

        val successState = viewModel.uiState.value
        assertFalse(successState.isLoading)
        assertNull(successState.error)
        assertEquals(2, successState.categories.size)

        coVerify(exactly = 2) { repository.getCategories() }
    }

    @Test
    fun `refreshCategories - ручное обновление категорий`() = runTest {
        val initialCategories = CategoryTestFixtures.createCategoryDtoList(count = 1, baseId = 1)
        coEvery { repository.getCategories() } returns flowOf(initialCategories)

        viewModel = CategoriesViewModel(repository)
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.categories.size)

        val updatedCategories = CategoryTestFixtures.createCategoryDtoList(count = 3, baseId = 1)

        coEvery { repository.refreshCategories() } coAnswers {
            coEvery { repository.getCategories() } returns flowOf(updatedCategories)
            Unit
        }

        viewModel.refreshCategories()

        viewModel.retry()

        advanceUntilIdle()

        val finalState = viewModel.uiState.value
        assertFalse(finalState.isLoading)
        assertEquals(3, finalState.categories.size)

        coVerify(exactly = 1) { repository.refreshCategories() }
    }
}