package com.yourcompany.recipecomposeapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.yourcompany.recipecomposeapp.core.network.NetworkConfig
import com.yourcompany.recipecomposeapp.data.model.CategoryDto
import com.yourcompany.recipecomposeapp.data.model.RecipeDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    private var deepLinkIntent by mutableStateOf<Intent?>(null)
    private val TAG = "MainActivity"

    private val recipesApiService = NetworkConfig.recipesApiService

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var networkJobs: List<Job> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d(TAG, "Метод onCreate() выполняется на потоке: ${Thread.currentThread().name}")

        val categoriesJob = coroutineScope.launch {
            Log.d(TAG, "Выполняю запрос категорий на корутине: ${Thread.currentThread().name}")
            executeCategoryRequest()
        }

        networkJobs = listOf(categoriesJob)

        handleDeepLinkIntent(intent)
        setContent {
            RecipesApp(deepLinkIntent = deepLinkIntent)

            LaunchedEffect(Unit) {

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        networkJobs.forEach { it.cancel() }
        Log.d(TAG, "Все сетевые корутины отменены")
    }

    private suspend fun executeCategoryRequest() {
        try {
            val categories = recipesApiService.getCategories()

            Log.d(TAG, "=== УСПЕШНО ПОЛУЧЕНО ${categories.size} КАТЕГОРИЙ ЧЕРЕЗ RETROFIT ===")
            Log.d(TAG, "Запрос выполнен на корутине: ${Thread.currentThread().name}")

            processCategories(categories)

        } catch (e: Exception) {

            Log.e(TAG, "Ошибка запроса категорий через Retrofit: ${e.message}", e)
        }
    }

    private suspend fun processCategories(categories: List<CategoryDto>) {
        try {
            Log.d(TAG, "Количество полученных категорий: ${categories.size}")

            categories.forEachIndexed { index, category ->
                Log.d(TAG, "${index + 1}. ${category.title} (ID: ${category.id})")
            }

            val recipeJobs = categories.map { category ->
                coroutineScope.async {
                    Log.d(TAG, "Запускаю запрос рецептов для категории '${category.title}' на корутине: ${Thread.currentThread().name}")
                    fetchRecipesForCategory(category)
                }
            }

            recipeJobs.forEach { it.await() }
            Log.d(TAG, "Все запросы рецептов завершены")

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка обработки данных категорий: ${e.message}", e)
        }
    }

    private suspend fun fetchRecipesForCategory(category: CategoryDto) {
        try {
            Log.d(TAG, "Выполняю Retrofit запрос рецептов для категории '${category.title}'")

            val recipes = recipesApiService.getRecipesByCategory(category.id)

            Log.d(TAG, "Категория '${category.title}': получено ${recipes.size} рецептов")

            recipes.firstOrNull()?.let { firstRecipe ->
                Log.d(TAG, "Первый рецепт в категории '${category.title}': ${firstRecipe.title}, ингредиентов: ${firstRecipe.ingredients.size}")
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

@Preview(showBackground = true)
@Composable
fun RecipeComposeAppPreview() {
    RecipesApp(deepLinkIntent = null)
}
