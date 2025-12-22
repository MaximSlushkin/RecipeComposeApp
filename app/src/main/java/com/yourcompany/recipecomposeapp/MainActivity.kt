package com.yourcompany.recipecomposeapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.yourcompany.recipecomposeapp.data.model.CategoryDto
import com.yourcompany.recipecomposeapp.data.model.RecipeDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
    private var deepLinkIntent by mutableStateOf<Intent?>(null)
    private val TAG = "MainActivity"
    private val jsonParser = Json { ignoreUnknownKeys = true }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    private val threadPool: ExecutorService = Executors.newFixedThreadPool(10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d(TAG, "Метод onCreate() выполняется на потоке: ${Thread.currentThread().name}")

        thread {
            Log.d(TAG, "Выполняю запрос категорий на потоке: ${Thread.currentThread().name}")
            executeCategoryRequest()
        }

        handleDeepLinkIntent(intent)
        setContent { RecipesApp(deepLinkIntent = deepLinkIntent) }
    }

    override fun onDestroy() {
        super.onDestroy()

        threadPool.shutdown()
        Log.d(TAG, "Thread pool shutdown initiated")
    }

    private fun executeCategoryRequest() {
        try {

            val request = Request.Builder()
                .url("https://recipes.androidsprint.ru/api/category")
                .addHeader("Accept", "application/json")
                .get()
                .build()

            Log.d(TAG, "Выполняю запрос категорий с OkHttp: ${request.url}")

            val response: Response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {

                val responseBody = response.body?.string()

                responseBody?.let {
                    Log.d(TAG, "=== ТЕЛО ОТВЕТА ОТ ОКHTTP ===\n$it\n=== КОНЕЦ ТЕЛА ОТВЕТА ===")
                    processCategories(it)
                }
            } else {
                Log.e(TAG, "Ошибка HTTP при запросе категорий: ${response.code} - ${response.message}")

                val errorBody = response.body?.string()
                Log.e(TAG, "Тело ошибки: $errorBody")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка запроса категорий с OkHttp: ${e.message}", e)
        }
    }

    private fun processCategories(jsonString: String) {
        try {

            val categories: List<CategoryDto> = jsonParser.decodeFromString(jsonString)

            Log.d(TAG, "Количество полученных категорий: ${categories.size}")

            categories.forEachIndexed { index, category ->
                Log.d(TAG, "${index + 1}. ${category.title} (ID: ${category.id})")
            }

            categories.forEach { category ->
                threadPool.execute {
                    Log.d(TAG, "Запускаю запрос рецептов для категории '${category.title}' на потоке: ${Thread.currentThread().name}")
                    fetchRecipesForCategory(category)
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка обработки данных категорий: ${e.message}", e)
        }
    }

    private fun fetchRecipesForCategory(category: CategoryDto) {
        try {

            val url = "https://recipes.androidsprint.ru/api/category/${category.id}/recipes"


            val request = Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .get()
                .build()

            Log.d(TAG, "Запрос рецептов для категории '${category.title}': $url")

            val response: Response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()

                responseBody?.let {
                    try {

                        val recipesResponse = jsonParser.decodeFromString<RecipesResponse>(it)
                        Log.d(TAG, "Категория '${category.title}': получено ${recipesResponse.recipes.size} рецептов на потоке ${Thread.currentThread().name}")

                        recipesResponse.recipes.firstOrNull()?.let { firstRecipe ->
                            Log.d(TAG, "Первый рецепт в категории '${category.title}': ${firstRecipe.title}, ингредиентов: ${firstRecipe.ingredients.size}")
                        }
                    } catch (e: Exception) {

                        Log.e(TAG, "Ошибка парсинга рецептов для категории '${category.title}': ${e.message}", e)
                        Log.d(TAG, "Категория '${category.title}': получен ответ размером ${it.length} символов")
                    }
                }

            } else {
                Log.e(TAG, "Ошибка HTTP при запросе рецептов для категории '${category.title}': ${response.code} - ${response.message}")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка запроса рецептов для категории '${category.title}': ${e.message}", e)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLinkIntent(intent)
        setIntent(intent)
    }

    private fun handleDeepLinkIntent(intent: Intent?) {
        intent?.data?.let { deepLinkIntent = intent }
    }
}

@Serializable
data class RecipesResponse(
    val recipes: List<RecipeDto>
)

@Preview(showBackground = true)
@Composable
fun RecipeComposeAppPreview() {
    RecipesApp(deepLinkIntent = null)
}