package net.lag129.ferret.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class HtmlParserTest : StringSpec() {

    init {
        "empty html" {
            val html = ""
            val result = parseHtml(html)
            result shouldBe emptyList()
        }

        "anchor element" {
            val html = "<a href=\"https://example.com\">Example</a>"
            val result = parseHtml(html)
            result shouldBe listOf(HtmlTag.Link("Example", "https://example.com"))
        }

        "break element" {
            val html = "<br>"
            val result = parseHtml(html)
            result shouldBe listOf(HtmlTag.Break)
        }

        "simple paragraph" {
            val html = "<p>Hello, World!</p>"
            val result = parseHtml(html)
            result shouldBe listOf(HtmlTag.Text("Hello, World!"))
        }

        "complex paragraph" {
            val html = "<p>Hello, <a href=\"https://example.com\">World!</a></p>"
            val result = parseHtml(html)
            result shouldBe listOf(
                HtmlTag.Text("Hello, "),
                HtmlTag.Link("World!", "https://example.com")
            )
        }
    }
}
