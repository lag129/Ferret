package net.lag129.ferret.ui.compose

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import net.lag129.ferret.model.Post
import net.lag129.ferret.utils.createEmojiInlineContent
import net.lag129.ferret.utils.parseEmojisToAnnotatedString
import net.lag129.ferret.utils.parseHtmlToAnnotatedString

@Composable
fun HtmlText(
    body: String,
    modifier: Modifier = Modifier,
    emojis: List<Post.Emoji>? = null,
    fontWeight: FontWeight? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current
) {
    val linkColor = MaterialTheme.colorScheme.primary

    val annotatedString = remember(body, emojis, linkColor) {
        val html = parseHtmlToAnnotatedString(
            html = body,
            urlSpanStyle = SpanStyle(
                color = linkColor,
                textDecoration = TextDecoration.Underline
            )
        )
        emojis?.let { parseEmojisToAnnotatedString(html, it) } ?: html
    }

    val inlineContent = createEmojiInlineContent(emojis.orEmpty())

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
