package net.lag129.ferret

import androidx.compose.ui.text.AnnotatedString
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.lag129.ferret.api.entity.CustomEmoji

class EmojiParserTest : StringSpec() {

    init {

        "empty emojis list" {
            val text = ":smile:"
            val emojis = emptyList<CustomEmoji>()
            val result = parseEmojisToAnnotatedString(AnnotatedString(text), emojis)
            result.text shouldBe text
        }

        "emojis list" {
            val text = ":smile:"
            val emojis = listOf(
                CustomEmoji(
                    shortcode = "smile",
                    url = "https://example.com/smile.png",
                    staticUrl = "https://example.com/smile.png",
                    visibleInPicker = true
                )
            )
            val result = parseEmojisToAnnotatedString(AnnotatedString(text), emojis)
            result.text shouldBe ""
        }
    }
}
