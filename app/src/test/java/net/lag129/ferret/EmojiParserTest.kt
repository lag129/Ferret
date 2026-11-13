package net.lag129.ferret

import androidx.compose.ui.text.AnnotatedString
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.lag129.ferret.model.Post
import net.lag129.ferret.utils.parseEmojisToAnnotatedString

class EmojiParserTest : StringSpec() {

    init {

        "empty emojis list" {
            val text = ":smile:"
            val emojis = emptyList<Post.Emoji>()
            val result = parseEmojisToAnnotatedString(AnnotatedString(text), emojis)
            result.text shouldBe text
        }

        "emojis list" {
            val text = ":smile:"
            val emojis = listOf(
                Post.Emoji(
                    shortcode = "smile",
                    url = "https://example.com/smile.png",
                )
            )
            val result = parseEmojisToAnnotatedString(AnnotatedString(text), emojis)
            result.text shouldBe ""
        }
    }
}
