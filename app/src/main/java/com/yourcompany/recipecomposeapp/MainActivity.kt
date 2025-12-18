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
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    private var deepLinkIntent by mutableStateOf<Intent?>(null)
    private val TAG = "MainActivity"
    private val jsonParser = Json { ignoreUnknownKeys = true }

    private val threadPool: ExecutorService = Executors.newFixedThreadPool(10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d(TAG, "Метод onCreate() выполняется на потоке: ${Thread.currentThread().name}")

        threadPool.execute {
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
            val connection = URL("https://recipes.androidsprint.ru/api/category")
                .openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
                setRequestProperty("Accept", "application/json")
            }

            val jsonResponse = if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().use { it.readText() }.also {
                    Log.d(TAG, "=== ТЕЛО ОТВЕТА ===\n$it\n=== КОНЕЦ ТЕЛА ОТВЕТА ===")
                }
            } else {
                Log.e(TAG, "Ошибка HTTP при запросе категорий: ${connection.responseCode}")
                null
            }

            jsonResponse?.let {
                processCategories(it)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка запроса категорий: ${e.message}", e)
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
            val url = URL("https://recipes.androidsprint.ru/api/category/${category.id}/recipes")
            val connection = url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
                setRequestProperty("Accept", "application/json")
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }

                try {
                    val recipesResponse = jsonParser.decodeFromString<RecipesResponse>(response)
                    Log.d(TAG, "Категория '${category.title}': получено ${recipesResponse.recipes.size} рецептов на потоке ${Thread.currentThread().name}")

                    recipesResponse.recipes.firstOrNull()?.let { firstRecipe ->
                        Log.d(TAG, "Первый рецепт в категории '${category.title}': ${firstRecipe.title}, ингредиентов: ${firstRecipe.ingredients.size}")
                    }

                } catch (e: Exception) {

                    Log.e(TAG, "Ошибка парсинга рецептов для категории '${category.title}': ${e.message}", e)
                    Log.d(TAG, "Категория '${category.title}': получен ответ размером ${response.length} символов")
                }
            } else {
                Log.e(TAG, "Ошибка HTTP при запросе рецептов для категории '${category.title}': ${connection.responseCode}")
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