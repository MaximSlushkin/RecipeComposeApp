package com.yourcompany.recipecomposeapp.core.network

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.yourcompany.recipecomposeapp.core.network.api.RecipesApiService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object NetworkConfig {

    var isDebug: Boolean = false
    const val BASE_URL = "https://recipes.androidsprint.ru/api/"

    private const val TAG = "NetworkConfig"
    private const val LOG_TAG = "HTTP_LOG"

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
        explicitNulls = false
    }

    private val jsonContentType = "application/json".toMediaType()

    private fun createOkHttpClient(): OkHttpClient {
        Log.d(TAG, "Creating OkHttpClient with debug mode: $isDebug")

        val builder = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)

        if (isDebug) {
            Log.d(TAG, "Adding HttpLoggingInterceptor with BODY level")

            val loggingInterceptor = HttpLoggingInterceptor { message ->
                Log.d(LOG_TAG, message)
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            builder.addInterceptor(loggingInterceptor)

            builder.addNetworkInterceptor(createNetworkInterceptor())
        }

        builder.addInterceptor(createCommonHeadersInterceptor())

        return builder.build()
    }

    private fun createNetworkInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val startTime = System.nanoTime()

            Log.d(LOG_TAG, " Sending request: ${request.method} ${request.url}")
            Log.d(LOG_TAG, " Headers: ${request.headers}")

            try {
                val response = chain.proceed(request)
                val endTime = System.nanoTime()
                val durationMs = (endTime - startTime) / 1_000_000

                Log.d(LOG_TAG, " Received response: ${response.code} ${response.message}")
                Log.d(LOG_TAG, " Response time: ${durationMs}ms")
                Log.d(LOG_TAG, " Response headers: ${response.headers}")

                response
            } catch (e: Exception) {
                val endTime = System.nanoTime()
                val durationMs = (endTime - startTime) / 1_000_000

                Log.e(LOG_TAG, " Request failed after ${durationMs}ms", e)
                throw e
            }
        }
    }

    private fun createCommonHeadersInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()

            val requestWithHeaders = originalRequest.newBuilder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("User-Agent", "RecipeComposeApp/1.0")
                .build()

            chain.proceed(requestWithHeaders)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit: Retrofit by lazy {
        Log.d(TAG, "Initializing Retrofit with base URL: $BASE_URL")

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(jsonParser.asConverterFactory(jsonContentType))
            .build()
    }

    val recipesApiService: RecipesApiService by lazy {
        retrofit.create(RecipesApiService::class.java).also {
            Log.d(TAG, "RecipesApiService initialized")
        }
    }

    fun setDebugMode(debug: Boolean) {
        isDebug = debug
        Log.d(TAG, "Debug mode set to: $debug")
    }
}