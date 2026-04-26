package net.lag129.ferret

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.lag129.ferret.utils.parseHtmlToAnnotatedString

class HtmlParserTest : StringSpec() {

    init {

        "empty html" {
            val html = ""
            val result = parseHtmlToAnnotatedString(html)
            result.text shouldBe ""
        }

        "anchor element" {
            val html = "<a href=\"https://example.com\">Example</a>"
            val result = parseHtmlToAnnotatedString(html)
            result.text shouldBe "Example"
        }

        "break element" {
            val html = "<br>"
            val result = parseHtmlToAnnotatedString(html)
            result.text shouldBe "\n"
        }

        "simple paragraph" {
            val html = "<p>Hello, World!</p>"
            val result = parseHtmlToAnnotatedString(html)
            result.text shouldBe "Hello, World!"
        }

        "complex paragraph" {
            val html = "<p>Hello, <a href=\"https://example.com\">World!</a></p>"
            val result = parseHtmlToAnnotatedString(html)
            result.text shouldBe "Hello, World!"
        }
    }
}
