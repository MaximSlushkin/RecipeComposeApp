package com.yourcompany.recipecomposeapp

import com.yourcompany.recipecomposeapp.BuildConfig
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.yourcompany.recipecomposeapp.core.network.NetworkConfig

class MainActivity : ComponentActivity() {
    private var deepLinkIntent by mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NetworkConfig.setDebugMode(BuildConfig.DEBUG)

        handleDeepLinkIntent(intent)
        setContent {
            RecipesApp(deepLinkIntent = deepLinkIntent)
        }

        android.util.Log.d("MainActivity",
            "App started. BuildConfig.DEBUG: ${BuildConfig.DEBUG}")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLinkIntent(intent)
        setIntent(intent)
    }

    private fun handleDeepLinkIntent(intent: Intent?) {
        intent?.data?.let {
            deepLinkIntent = intent
            android.util.Log.d("MainActivity", "Deep link handled: ${intent.data}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeComposeAppPreview() {
    RecipesApp(deepLinkIntent = null)
}