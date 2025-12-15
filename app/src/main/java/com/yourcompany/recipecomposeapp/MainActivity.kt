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
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    private var deepLinkIntent by mutableStateOf<Intent?>(null)
    private val TAG = "MainActivity"
    private val jsonParser = Json { ignoreUnknownKeys = true }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d(TAG, "Метод onCreate() выполняется на потоке: ${Thread.currentThread().name}")

        Thread {
            Log.d(TAG, "Выполняю запрос на потоке: ${Thread.currentThread().name}")

            executeCategoryRequest()
        }.start()

        handleDeepLinkIntent(intent)
        setContent { RecipesApp(deepLinkIntent = deepLinkIntent) }
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

            // 5. Выводим содержимое тела ответа в консоль
            val jsonResponse = if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().use { it.readText() }.also {
                    Log.d(TAG, "=== ТЕЛО ОТВЕТА ===\n$it\n=== КОНЕЦ ТЕЛА ОТВЕТА ===")
                }
            } else {
                Log.e(TAG, "Ошибка HTTP: ${connection.responseCode}")
                null
            }

            jsonResponse?.let { processCategories(it) }

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка запроса: ${e.message}")
        }
    }

    private fun processCategories(jsonString: String) {
        try {
            val categories: List<CategoryDto> = jsonParser.decodeFromString(jsonString)

            Log.d(TAG, "Количество полученных категорий: ${categories.size}")

            categories.forEachIndexed { index, category ->
                Log.d(TAG, "${index + 1}. ${category.title}")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка обработки данных: ${e.message}")
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

@Preview(showBackground = true)
@Composable
fun RecipeComposeAppPreview() {
    RecipesApp(deepLinkIntent = null)
}