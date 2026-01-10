package net.lag129.ferret.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.crossfade
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import net.lag129.ferret.ui.theme.FerretTheme

@Composable
fun MediaScreen(
    mediaUrl: String,
    modifier: Modifier = Modifier,
    description: String? = null
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        ZoomableAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(mediaUrl)
                .crossfade(1_000)
                .build(),
            imageLoader = LocalContext.current.imageLoader,
            contentDescription = description ?: "",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
private fun MediaScreenPreview() {
    FerretTheme {
        MediaScreen(
            mediaUrl = ""
        )
    }
}
