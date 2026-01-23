package net.lag129.ferret.compose

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import net.lag129.ferret.api.entity.Attachment

@Composable
fun SharedTransitionScope.MediaAttachmentCard(
    @SuppressLint("ComposeUnstableCollections")
    mediaAttachments: List<Attachment>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    onMediaClick: ((mediaUrl: String, description: String?) -> Unit)? = null
) {
    val attachmentsSize = mediaAttachments.size

    when (attachmentsSize) {
        0 -> return
        1 -> SingleMediaAttachmentCard(
            media = mediaAttachments[0],
            animatedVisibilityScope = animatedVisibilityScope,
            onMediaClick = onMediaClick,
            modifier = modifier
        )

        2 -> DoubleMediaAttachmentCard(
            media = mediaAttachments,
            animatedVisibilityScope = animatedVisibilityScope,
            onMediaClick = onMediaClick,
            modifier = modifier
        )

        3 -> ThreeMediaLayout(
            media = mediaAttachments,
            animatedVisibilityScope = animatedVisibilityScope,
            onMediaClick = onMediaClick,
            modifier = modifier
        )

        else -> FourMediaLayout(
            media = mediaAttachments.take(4),
            animatedVisibilityScope = animatedVisibilityScope,
            onMediaClick = onMediaClick,
            modifier = modifier
        )
    }
}

@Composable
private fun SharedTransitionScope.SingleMediaAttachmentCard(
    media: Attachment,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    onMediaClick: ((mediaUrl: String, description: String?) -> Unit)? = null
) {
    MediaImage(
        media = media,
        animatedVisibilityScope = animatedVisibilityScope,
        modifier = modifier
            .aspectRatio(ratio = 1.618f)
            .fillMaxWidth(),
        onMediaClick = onMediaClick
    )
}

@Composable
private fun SharedTransitionScope.DoubleMediaAttachmentCard(
    @SuppressLint("ComposeUnstableCollections")
    media: List<Attachment>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    onMediaClick: ((mediaUrl: String, description: String?) -> Unit)? = null
) {
    Row(
        modifier = modifier
            .aspectRatio(ratio = 1.618f)
            .fillMaxWidth()
    ) {
        MediaImage(
            media = media[0],
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            onMediaClick = onMediaClick
        )

        Spacer(Modifier.padding(2.dp))

        MediaImage(
            media = media[1],
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            onMediaClick = onMediaClick
        )
    }
}

@Composable
private fun SharedTransitionScope.ThreeMediaLayout(
    @SuppressLint("ComposeUnstableCollections")
    media: List<Attachment>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    onMediaClick: ((mediaUrl: String, description: String?) -> Unit)?
) {
    Row(
        modifier = modifier
            .aspectRatio(1.618f)
            .fillMaxWidth()
    ) {
        MediaImage(
            media = media[0],
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            onMediaClick = onMediaClick
        )

        Spacer(Modifier.padding(2.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            MediaImage(
                media = media[1],
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.weight(1f),
                onMediaClick = onMediaClick
            )

            Spacer(Modifier.padding(2.dp))

            MediaImage(
                media = media[2],
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.weight(1f),
                onMediaClick = onMediaClick
            )
        }
    }
}

@Composable
private fun SharedTransitionScope.FourMediaLayout(
    @SuppressLint("ComposeUnstableCollections")
    media: List<Attachment>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    onMediaClick: ((mediaUrl: String, description: String?) -> Unit)?
) {
    Column(
        modifier = modifier
            .aspectRatio(1.618f)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            MediaImage(
                media = media[0],
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.weight(1f),
                onMediaClick = onMediaClick
            )

            Spacer(Modifier.padding(2.dp))

            MediaImage(
                media = media[1],
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.weight(1f),
                onMediaClick = onMediaClick
            )
        }

        Spacer(Modifier.padding(2.dp))

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            MediaImage(
                media = media[2],
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.weight(1f),
                onMediaClick = onMediaClick
            )

            Spacer(Modifier.padding(2.dp))

            MediaImage(
                media = media[3],
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.weight(1f),
                onMediaClick = onMediaClick
            )
        }
    }
}

@Composable
private fun SharedTransitionScope.MediaImage(
    media: Attachment,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    onMediaClick: ((mediaUrl: String, description: String?) -> Unit)?
) {
    val context = LocalContext.current
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    val imageRequest = remember(media.url) {
        ImageRequest.Builder(context)
            .data(media.url)
            .build()
    }

    AsyncImage(
        model = imageRequest,
        contentDescription = media.description,
        contentScale = ContentScale.Crop,
        placeholder = ColorPainter(surfaceVariant),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .border(
                BorderStroke(0.4.dp, MaterialTheme.colorScheme.outlineVariant),
                RoundedCornerShape(10.dp)
            )
            .clickable {
                onMediaClick?.invoke(media.url, media.description)
            }
            .sharedElement(
                sharedContentState = rememberSharedContentState(key = media.url),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = { _, _ ->
                    tween(durationMillis = 300)
                }
            )
    )
}
