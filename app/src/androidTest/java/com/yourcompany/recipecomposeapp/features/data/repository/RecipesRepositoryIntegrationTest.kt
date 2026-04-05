package com.yourcompany.recipecomposeapp.features.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.yourcompany.recipecomposeapp.core.network.api.RecipesApiService
import com.yourcompany.recipecomposeapp.data.database.RecipesDatabase
import com.yourcompany.recipecomposeapp.data.database.entity.CategoryEntity
import com.yourcompany.recipecomposeapp.data.model.CategoryDto
import com.yourcompany.recipecomposeapp.data.repository.RecipesRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit

private val sharedJson = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    isLenient = true
    explicitNulls = false
}

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalSerializationApi::class)
@RunWith(AndroidJUnit4::class)
class RecipesRepositoryIntegrationTest {

    private lateinit var database: RecipesDatabase
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: RecipesApiService
    private lateinit var repository: RecipesRepositoryImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(context, RecipesDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(sharedJson.asConverterFactory("application/json".toMediaType()))
            .build()

        apiService = retrofit.create(RecipesApiService::class.java)

    }

    @After
    fun tearDown() {
        database.close()
        mockWebServer.shutdown()
    }

    @Test
    fun savesDataToCacheAfterSuccessfulApiCall() = runTest {
        repository = RecipesRepositoryImpl(
            apiService = apiService,
            database = database,
            externalScope = CoroutineScope(this.coroutineContext)
        )

        val expectedCategories = listOf(
            CategoryDto(1, "Завтраки", "Лёгкие завтраки", "breakfast.jpg"),
            CategoryDto(2, "Обеды", "Сытные обеды", "lunch.jpg")
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(sharedJson.encodeToString(expectedCategories))
                .addHeader("Content-Type", "application/json")
        )

        val resultFlow = repository.getCategories()

        val firstResult = resultFlow.first()
        assertTrue(firstResult.isEmpty())

        advanceUntilIdle()

        val cachedCategories = database.categoryDao().getAllCategories().first()
        assertEquals("Данные не сохранились в БД", 2, cachedCategories.size)
        assertEquals("Завтраки", cachedCategories[0].name)
        assertEquals("Обеды", cachedCategories[1].name)

        val finalResult = resultFlow.first()
        assertEquals(2, finalResult.size)
        assertEquals("Завтраки", finalResult[0].title)
        assertEquals("Обеды", finalResult[1].title)
    }

    @Test
    fun returnsCachedDataWhenApiFails() = runTest {
        repository = RecipesRepositoryImpl(
            apiService = apiService,
            database = database,
            externalScope = CoroutineScope(this.coroutineContext)
        )

        val cachedCategories = listOf(
            CategoryEntity(1, "Кешированные завтраки", "Описание", "img1.jpg"),
            CategoryEntity(2, "Кешированные обеды", "Описание", "img2.jpg")
        )
        database.categoryDao().insertCategories(cachedCategories)

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        val resultFlow = repository.getCategories()

        advanceUntilIdle()

        val result = resultFlow.first()
        assertEquals(2, result.size)
        assertEquals("Кешированные завтраки", result[0].title)
        assertEquals("Кешированные обеды", result[1].title)
    }
}