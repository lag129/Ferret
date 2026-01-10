package net.lag129.ferret.compose

import android.annotation.SuppressLint
import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import net.lag129.ferret.R
import net.lag129.ferret.api.entity.Attachment
import net.lag129.ferret.api.entity.CustomEmoji
import net.lag129.ferret.api.entity.PreviewCard
import net.lag129.ferret.createEmojiInlineContent
import net.lag129.ferret.emojisToAnnotatedString
import net.lag129.ferret.htmlToAnnotatedString
import net.lag129.ferret.ui.theme.FerretTheme
import kotlin.time.Clock
import kotlin.time.Instant

@Immutable
data class StatusCardData(
    val displayName: String,
    val userName: String,
    val avatarUrl: String,
    val createdAt: String,
    val content: String,
    val card: PreviewCard? = null,
    val displayNameEmojis: List<CustomEmoji>? = null,
    val emojis: List<CustomEmoji>? = null,
    val mediaAttachments: List<Attachment>? = null,
    val sensitive: Boolean,
    val spoilerText: String
)

@Composable
private fun ContentBox(
    content: String,
    modifier: Modifier = Modifier,
    @SuppressLint("ComposeUnstableCollections")
    emojis: List<CustomEmoji>? = null
) {
    SelectionContainer(
        modifier = modifier
    ) {
        val annotatedString = emojis?.let {
            emojisToAnnotatedString(
                htmlToAnnotatedString(content),
                it
            )
        } ?: run {
            htmlToAnnotatedString(content)
        }

        val inlineContent = mutableMapOf<String, InlineTextContent>()

        emojis?.let {
            inlineContent.putAll(
                createEmojiInlineContent(it, 20)
            )
        }

        Text(
            text = annotatedString,
            inlineContent = inlineContent,
            style = TextStyle(
                fontSize = 16.sp,
                lineBreak = LineBreak.Paragraph
            ),
        )
    }
}

@Composable
fun StatusCard(
    data: StatusCardData,
    modifier: Modifier = Modifier,
    onMediaClick: ((mediaUrl: String, description: String?) -> Unit)? = null
) {
    val (displayName, userName, avatarUrl, createdAt, content, card, displayNameEmojis, emojis, mediaAttachments, sensitive, spoilerText) = data

    val now = Clock.System.now().toEpochMilliseconds()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = displayName,
            modifier = Modifier
                .width(40.dp)
                .clip(RoundedCornerShape(30))
        )

        Spacer(modifier = Modifier.padding(6.dp))

        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    val annotatedString = displayNameEmojis?.let {
                        emojisToAnnotatedString(
                            htmlToAnnotatedString(displayName),
                            it
                        )
                    } ?: run {
                        htmlToAnnotatedString(content)
                    }

                    val inlineContent = mutableMapOf<String, InlineTextContent>()

                    displayNameEmojis?.let {
                        inlineContent.putAll(
                            createEmojiInlineContent(it, 20)
                        )
                    }

                    Text(
                        text = annotatedString,
                        inlineContent = inlineContent,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.alignByBaseline()
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    Text(
                        text = "@$userName",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.alignByBaseline()
                    )
                }

                val createdAt = Instant.parse(createdAt).toEpochMilliseconds()

                Text(
                    text = DateUtils.getRelativeTimeSpanString(
                        createdAt, now, 0
                    ).toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    maxLines = 1,
                    modifier = Modifier.alignByBaseline()
                )
            }

            var isSpoilerTextClicked by remember { mutableStateOf(false) }

            if (spoilerText.isNotBlank()) {
                Text(
                    text = spoilerText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(2.dp)
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .clickable { isSpoilerTextClicked = !isSpoilerTextClicked }
                )
            }

            AnimatedVisibility(
                visible = spoilerText.isBlank() || isSpoilerTextClicked,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                ContentBox(
                    content = content,
                    emojis = emojis
                )
            }

            if (!mediaAttachments.isNullOrEmpty()) {
                Spacer(modifier = Modifier.padding(8.dp))

                if (sensitive) {
                    var isBlurred by remember { mutableStateOf(true) }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        MediaAttachmentCard(
                            mediaAttachments = mediaAttachments,
                            onMediaClick = onMediaClick,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .blur(radius = if (isBlurred) 40.dp else 0.dp)
                        )

                        if (isBlurred) {
                            Text(
                                text = stringResource(R.string.sensitive),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                } else {
                    MediaAttachmentCard(
                        mediaAttachments = mediaAttachments,
                        onMediaClick = onMediaClick,
                    )
                }
            }

            if (card != null) {
                Spacer(modifier = Modifier.padding(8.dp))

                LinkPreviewCard(
                    url = card.url,
                    imageUrl = card.image,
                    title = card.title,
                    desc = card.description,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
private fun StatusCardPreview() {
    FerretTheme {
        StatusCard(
            StatusCardData(
                displayName = "ユーザー",
                userName = "user@example.com",
                createdAt = "2026-01-01T12:00:00Z",
                avatarUrl = "",
                content = "<p>ダミーテキスト<p>",
                sensitive = false,
                spoilerText = "",
            )
        )
    }
}
