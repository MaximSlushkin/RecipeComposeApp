package com.yourcompany.recipecomposeapp.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.transformations
import coil3.transform.RoundedCornersTransformation
import com.yourcompany.recipecomposeapp.R

@Composable
fun RecipeImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    cornerRadius: Float = 0f,
    showLoadingIndicator: Boolean = true
) {
    val context = LocalContext.current

    val imageRequest = remember(imageUrl, cornerRadius) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .size(coil3.size.Size.ORIGINAL)
            .crossfade(true)
            .crossfade(300)
            .apply {
                if (cornerRadius > 0) {
                    transformations(RoundedCornersTransformation(cornerRadius))
                }
            }
            .build()
    }

    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    val painter = rememberAsyncImagePainter(
        model = imageRequest,
        onState = { state ->
            when (state) {
                is AsyncImagePainter.State.Loading -> {
                    isLoading = true
                    hasError = false
                }

                is AsyncImagePainter.State.Success -> {
                    isLoading = false
                    hasError = false
                }

                is AsyncImagePainter.State.Error -> {
                    isLoading = false
                    hasError = true
                }

                else -> {
                    isLoading = false
                }
            }
        }
    )

    LaunchedEffect(imageUrl) {
        kotlinx.coroutines.delay(10000)
        if (isLoading) {
            isLoading = false
        }
    }

    Box(modifier = modifier) {
        AsyncImage(
            model = imageRequest,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale,
            placeholder = painterResource(R.drawable.img_placeholder),
            error = painterResource(R.drawable.img_error),
            onSuccess = { },
            onError = { }
        )

        if (isLoading && showLoadingIndicator) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }

        if (hasError && !isLoading) {
        }
    }
}

@Composable
fun SimpleRecipeImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    cornerRadius: Float = 0f
) {
    val context = LocalContext.current

    val imageRequest = remember(imageUrl, cornerRadius) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .crossfade(300)
            .apply {
                if (cornerRadius > 0) {
                    transformations(RoundedCornersTransformation(cornerRadius))
                }
            }
            .build()
    }

    AsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        placeholder = painterResource(R.drawable.img_placeholder),
        error = painterResource(R.drawable.img_error),
        modifier = modifier,
        contentScale = contentScale
    )
}