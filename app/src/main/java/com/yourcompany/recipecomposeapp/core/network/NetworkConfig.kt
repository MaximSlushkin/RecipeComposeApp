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

    private var _isDebug: Boolean = false
    private val isDebug: Boolean get() = _isDebug

    const val BASE_URL = "https://recipes.androidsprint.ru/api/"

    private const val TAG = "NetworkConfig"
    private const val LOG_TAG = "HTTP_LOG"

    private val jsonParser by lazy {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
            explicitNulls = false
        }
    }

    private val jsonContentType by lazy { "application/json".toMediaType() }

    private lateinit var _okHttpClient: OkHttpClient
    private lateinit var _retrofit: Retrofit
    private lateinit var _recipesApiService: RecipesApiService

    fun initialize(debug: Boolean) {
        _isDebug = debug
        Log.d(TAG, "NetworkConfig initialized with debug mode: $debug")

        _okHttpClient = createOkHttpClient()
        _retrofit = createRetrofit()
        _recipesApiService = _retrofit.create(RecipesApiService::class.java)

        Log.d(TAG, "Network components initialized successfully")
    }

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
        }

        builder.addInterceptor(createCommonHeadersInterceptor())

        return builder.build()
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
    private fun createRetrofit(): Retrofit {
        Log.d(TAG, "Creating Retrofit with base URL: $BASE_URL")

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(_okHttpClient)
            .addConverterFactory(jsonParser.asConverterFactory(jsonContentType))
            .build()
    }

    val recipesApiService: RecipesApiService
        get() {
            if (!::_recipesApiService.isInitialized) {
                throw IllegalStateException(
                    "NetworkConfig must be initialized before accessing recipesApiService. " +
                            "Call NetworkConfig.initialize() first."
                )
            }
            return _recipesApiService
        }

    val isInitialized: Boolean
        get() = ::_recipesApiService.isInitialized
}