package net.lag129.ferret.compose

import android.annotation.SuppressLint
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import net.lag129.ferret.api.entity.Attachment

@Composable
fun MediaAttachmentCard(
    @SuppressLint("ComposeUnstableCollections")
    mediaAttachments: List<Attachment>,
    onMediaClick: ((mediaUrl: String, description: String?) -> Unit)? = null
) {
    val attachmentsSize = mediaAttachments.size

    when (attachmentsSize) {
        0 -> return
        1 -> SingleMediaAttachmentCard(
            media = mediaAttachments[0],
            onMediaClick = onMediaClick
        )

        2 -> DoubleMediaAttachmentCard(
            media = mediaAttachments,
            onMediaClick = onMediaClick
        )

        3 -> ThreeMediaLayout(
            media = mediaAttachments,
            onMediaClick = onMediaClick
        )

        else -> FourMediaLayout(
            media = mediaAttachments.take(4),
            onMediaClick = onMediaClick
        )
    }
}

@Composable
private fun SingleMediaAttachmentCard(
    media: Attachment,
    modifier: Modifier = Modifier,
    onMediaClick: ((mediaUrl: String, description: String?) -> Unit)? = null
) {
    MediaImage(
        media = media,
        modifier = modifier.fillMaxWidth(),
        onMediaClick = onMediaClick
    )
}

@Composable
private fun DoubleMediaAttachmentCard(
    @SuppressLint("ComposeUnstableCollections")
    media: List<Attachment>,
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
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            onMediaClick = onMediaClick
        )

        Spacer(Modifier.padding(2.dp))

        MediaImage(
            media = media[1],
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            onMediaClick = onMediaClick
        )
    }
}

@Composable
private fun ThreeMediaLayout(
    @SuppressLint("ComposeUnstableCollections")
    media: List<Attachment>,
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
                modifier = Modifier.weight(1f),
                onMediaClick = onMediaClick
            )

            Spacer(Modifier.padding(2.dp))

            MediaImage(
                media = media[2],
                modifier = Modifier.weight(1f),
                onMediaClick = onMediaClick
            )
        }
    }
}

@Composable
private fun FourMediaLayout(
    @SuppressLint("ComposeUnstableCollections")
    media: List<Attachment>,
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
                modifier = Modifier.weight(1f),
                onMediaClick = onMediaClick
            )

            Spacer(Modifier.padding(2.dp))

            MediaImage(
                media = media[1],
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
                modifier = Modifier.weight(1f),
                onMediaClick = onMediaClick
            )

            Spacer(Modifier.padding(2.dp))

            MediaImage(
                media = media[3],
                modifier = Modifier.weight(1f),
                onMediaClick = onMediaClick
            )
        }
    }
}

@Composable
private fun MediaImage(
    media: Attachment,
    modifier: Modifier = Modifier,
    onMediaClick: ((mediaUrl: String, description: String?) -> Unit)?
) {
    AsyncImage(
        model = media.url,
        contentDescription = media.description,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .border(
                BorderStroke(0.4.dp, MaterialTheme.colorScheme.outlineVariant),
                RoundedCornerShape(10.dp)
            )
            .clickable {
                onMediaClick?.invoke(media.url, media.description)
            }
    )
}
