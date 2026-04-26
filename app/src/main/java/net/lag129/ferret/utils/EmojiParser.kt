package net.lag129.ferret.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.collections.immutable.ImmutableList
import net.lag129.ferret.model.CustomEmoji

@Composable
fun emojisToAnnotatedString(
    annotatedString: AnnotatedString,
    emojis: ImmutableList<CustomEmoji>
): AnnotatedString {

    return parseEmojisToAnnotatedString(annotatedString, emojis)
}

fun parseEmojisToAnnotatedString(
    annotatedString: AnnotatedString,
    emojis: List<CustomEmoji>
): AnnotatedString {

    val plainText = annotatedString.text
    val shortcodePattern = Regex(":([a-zA-Z0-9_]+):")

    val emojiMap = emojis.associateBy { it.shortcode }

    return buildAnnotatedString {
        var lastIndex = 0

        for (matchResult in shortcodePattern.findAll(plainText)) {
            val shortcode = matchResult.groupValues[1]
            val emoji = emojiMap[shortcode]

            append(annotatedString.subSequence(lastIndex, matchResult.range.first))

            if (emoji != null) {
                appendInlineContent(shortcode, shortcode)
                append("\u200B")
            } else {
                append(
                    annotatedString.subSequence(
                        matchResult.range.first,
                        matchResult.range.last + 1
                    )
                )
            }

            lastIndex = matchResult.range.last + 1
        }

        if (lastIndex < plainText.length) {
            append(annotatedString.subSequence(lastIndex, plainText.length))
        }
    }
}

@Composable
fun createEmojiInlineContent(
    emojis: ImmutableList<CustomEmoji>,
    size: Int = 20
): Map<String, InlineTextContent> {

    val inlineContent = mutableMapOf<String, InlineTextContent>()

    emojis.forEach { emoji ->
        val width = emoji.width
            ?.times(size)
            ?.div(emoji.height ?: size) ?: size

        inlineContent[emoji.shortcode] = InlineTextContent(
            Placeholder(width.sp, size.sp, PlaceholderVerticalAlign.Center)
        ) {
            AsyncImage(
                model = emoji.url,
                contentDescription = emoji.shortcode,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    return inlineContent
}
