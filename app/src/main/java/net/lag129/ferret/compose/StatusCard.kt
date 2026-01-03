package net.lag129.ferret.compose

import android.annotation.SuppressLint
import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import net.lag129.ferret.api.entity.CustomEmoji
import net.lag129.ferret.api.entity.PreviewCard
import net.lag129.ferret.createEmojiInlineContent
import net.lag129.ferret.emojisToAnnotatedString
import net.lag129.ferret.htmlToAnnotatedString
import net.lag129.ferret.ui.theme.FerretTheme
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun StatusCard(
    displayName: String,
    userName: String,
    avatarUrl: String,
    createdAt: String,
    content: String,
    modifier: Modifier = Modifier,
    card: PreviewCard? = null,
    @SuppressLint("ComposeUnstableCollections")
    displayNameEmojis: List<CustomEmoji>? = null,
    @SuppressLint("ComposeUnstableCollections")
    emojis: List<CustomEmoji>? = null,
) {
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

            SelectionContainer {
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
            displayName = "ユーザー",
            userName = "user@example.com",
            createdAt = "2026-01-01T12:00:00Z",
            avatarUrl = "",
            content = "<p>ダミーテキスト<p>"
        )
    }
}
