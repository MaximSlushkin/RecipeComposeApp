package com.yourcompany.recipecomposeapp.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
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

    val imageRequest = ImageRequest.Builder(context)
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

    val painter = rememberAsyncImagePainter(
        model = imageRequest,
        onState = { state ->
            when (state) {
                is AsyncImagePainter.State.Loading -> {
                }

                is AsyncImagePainter.State.Success -> {
                }

                is AsyncImagePainter.State.Error -> {
                }

                else -> {
                }
            }
        }
    )

    val isLoading = painter.state is AsyncImagePainter.State.Loading
    val hasError = painter.state is AsyncImagePainter.State.Error

    Box(modifier = modifier) {

        AsyncImage(
            model = imageRequest,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale,
            placeholder = painterResource(R.drawable.img_placeholder),
            error = painterResource(R.drawable.img_error),
            onLoading = { },
            onSuccess = { },
            onError = {  }
        )

        if (isLoading && showLoadingIndicator) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }

        if (hasError) {
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

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .crossfade(300)
            .apply {
                if (cornerRadius > 0) {
                    transformations(RoundedCornersTransformation(cornerRadius))
                }
            }
            .build(),
        contentDescription = contentDescription,
        placeholder = painterResource(R.drawable.img_placeholder),
        error = painterResource(R.drawable.img_error),
        modifier = modifier,
        contentScale = contentScale
    )
}