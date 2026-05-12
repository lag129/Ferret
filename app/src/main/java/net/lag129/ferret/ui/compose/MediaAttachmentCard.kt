package net.lag129.ferret.ui.compose

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import net.lag129.ferret.model.Attachment

@Composable
fun SharedTransitionScope.MediaAttachmentCard(
    mediaAttachments: ImmutableList<Attachment>,
    onClickMedia: (mediaUrl: String, description: String?) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val attachmentsSize = mediaAttachments.size

    when (attachmentsSize) {
        0 -> return
        1 -> SingleMediaAttachmentCard(
            media = mediaAttachments[0],
            animatedVisibilityScope = animatedVisibilityScope,
            onClickMedia = onClickMedia,
            modifier = modifier
        )

        2 -> DoubleMediaAttachmentCard(
            media = mediaAttachments,
            animatedVisibilityScope = animatedVisibilityScope,
            onClickMedia = onClickMedia,
            modifier = modifier
        )

        3 -> ThreeMediaLayout(
            media = mediaAttachments,
            animatedVisibilityScope = animatedVisibilityScope,
            onClickMedia = onClickMedia,
            modifier = modifier
        )

        else -> FourMediaLayout(
            media = mediaAttachments.take(4).toImmutableList(),
            animatedVisibilityScope = animatedVisibilityScope,
            onClickMedia = onClickMedia,
            modifier = modifier
        )
    }
}

@Composable
private fun SharedTransitionScope.SingleMediaAttachmentCard(
    media: Attachment,
    onClickMedia: (mediaUrl: String, description: String?) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    MediaImage(
        media = media,
        animatedVisibilityScope = animatedVisibilityScope,
        modifier = modifier
            .aspectRatio(ratio = 1.618f)
            .fillMaxWidth(),
        onClickMedia = onClickMedia
    )
}

@Composable
private fun SharedTransitionScope.DoubleMediaAttachmentCard(
    media: ImmutableList<Attachment>,
    onClickMedia: (mediaUrl: String, description: String?) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
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
            onClickMedia = onClickMedia
        )

        Spacer(Modifier.width(2.dp))

        MediaImage(
            media = media[1],
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            onClickMedia = onClickMedia
        )
    }
}

@Composable
private fun SharedTransitionScope.ThreeMediaLayout(
    media: ImmutableList<Attachment>,
    onClickMedia: (mediaUrl: String, description: String?) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
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
            onClickMedia = onClickMedia
        )

        Spacer(Modifier.width(2.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            MediaImage(
                media = media[1],
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.weight(1f),
                onClickMedia = onClickMedia
            )

            Spacer(Modifier.height(2.dp))

            MediaImage(
                media = media[2],
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.weight(1f),
                onClickMedia = onClickMedia
            )
        }
    }
}

@Composable
private fun SharedTransitionScope.FourMediaLayout(
    media: ImmutableList<Attachment>,
    onClickMedia: (mediaUrl: String, description: String?) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
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
                onClickMedia = onClickMedia
            )

            Spacer(Modifier.width(2.dp))

            MediaImage(
                media = media[1],
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.weight(1f),
                onClickMedia = onClickMedia
            )
        }

        Spacer(Modifier.height(2.dp))

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            MediaImage(
                media = media[2],
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.weight(1f),
                onClickMedia = onClickMedia
            )

            Spacer(Modifier.width(2.dp))

            MediaImage(
                media = media[3],
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.weight(1f),
                onClickMedia = onClickMedia
            )
        }
    }
}

@Composable
private fun SharedTransitionScope.MediaImage(
    media: Attachment,
    onClickMedia: (mediaUrl: String, description: String?) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(media.previewUrl)
            .memoryCacheKey("${media.url}-key")
            .build(),
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
                onClickMedia.invoke(media.url, media.description)
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
