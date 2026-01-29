package net.lag129.ferret.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import net.lag129.ferret.api.entity.CustomEmoji
import net.lag129.ferret.createEmojiInlineContent
import net.lag129.ferret.emojisToAnnotatedString
import net.lag129.ferret.htmlToAnnotatedString

@Composable
fun HtmlText(
    body: String,
    modifier: Modifier = Modifier,
    @SuppressLint("ComposeUnstableCollections")
    emojis: List<CustomEmoji>? = null,
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
