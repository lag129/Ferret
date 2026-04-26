package net.lag129.ferret.ui.compose

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.collections.immutable.ImmutableList
import net.lag129.ferret.model.CustomEmoji
import net.lag129.ferret.utils.createEmojiInlineContent
import net.lag129.ferret.utils.emojisToAnnotatedString
import net.lag129.ferret.utils.htmlToAnnotatedString

@Composable
fun HtmlText(
    body: String,
    modifier: Modifier = Modifier,
    emojis: ImmutableList<CustomEmoji>? = null,
    fontWeight: FontWeight? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current
) {
    val annotatedString = emojis?.let {
        emojisToAnnotatedString(
            htmlToAnnotatedString(body),
            it
        )
    } ?: run {
        htmlToAnnotatedString(body)
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
        fontWeight = fontWeight,
        overflow = overflow,
        maxLines = maxLines,
        style = style,
        modifier = modifier
    )
}
