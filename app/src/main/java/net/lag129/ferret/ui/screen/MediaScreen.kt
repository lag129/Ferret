package net.lag129.ferret.ui.screen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.crossfade
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun SharedTransitionScope.MediaScreen(
    mediaUrl: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    description: String? = null
) {
    val zoomState = rememberZoomState()

    Scaffold { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(mediaUrl)
                    .crossfade(true)
                    .placeholderMemoryCacheKey("${mediaUrl}-key")
                    .memoryCacheKey(mediaUrl)
                    .build(),
                imageLoader = LocalContext.current.imageLoader,
                contentDescription = description ?: "",
                contentScale = ContentScale.Fit,
                onSuccess = { state ->
                    zoomState.setContentSize(state.painter.intrinsicSize)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .zoomable(zoomState)
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = mediaUrl),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            tween(durationMillis = 300)
                        }
                    )
            )
        }
    }
}
