package net.lag129.ferret.utils

import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser

sealed interface HtmlTag {
    data class Text(val text: String) : HtmlTag
    data class Link(val text: String, val url: String) : HtmlTag
    data object Break : HtmlTag
}

private enum class SpanState {
    INVISIBLE, ELLIPSIS, NORMAL
}

fun parseHtml(html: String): List<HtmlTag> {
    val tags = mutableListOf<HtmlTag>()
    val spanStack = mutableListOf<SpanState>()
    var linkUrl: String? = null
    val linkText = StringBuilder()

    fun appendText(text: String) {
        if (linkUrl != null) {
            linkText.append(text)
        } else {
            tags.add(HtmlTag.Text(text))
        }
    }

    val handler = object : KsoupHtmlHandler {
        override fun onOpenTag(
            name: String,
            attributes: Map<String, String>,
            isImplied: Boolean
        ) {
            when (name.lowercase()) {
                "p" -> {
                    if (tags.isNotEmpty()) {
                        tags.add(HtmlTag.Break)
                    }
                }

                "br" -> tags.add(HtmlTag.Break)
                "a" -> {
                    val href = attributes["href"]
                    if (!href.isNullOrBlank()) {
                        linkUrl = href
                        linkText.clear()
                    }
                }

                "span" -> {
                    val style = attributes["class"]?.trim() ?: ""
                    val spanState = when {
                        style.contains("invisible") -> SpanState.INVISIBLE
                        style.contains("ellipsis") -> SpanState.ELLIPSIS
                        else -> SpanState.NORMAL
                    }
                    spanStack.add(spanState)
                }
            }
        }

        override fun onText(text: String) {
            if (text.isBlank()) return
            if (spanStack.lastOrNull() == SpanState.INVISIBLE) return

            appendText(text)
        }

        override fun onCloseTag(name: String, isImplied: Boolean) {
            when (name.lowercase()) {
                "a" -> {
                    val url = linkUrl ?: return
                    tags.add(HtmlTag.Link(linkText.toString(), url))
                    linkUrl = null
                }

                "span" -> {
                    if (spanStack.isNotEmpty()) {
                        val state = spanStack.removeAt(spanStack.size - 1)
                        if (state == SpanState.ELLIPSIS) {
                            appendText("...")
                        }
                    }
                }
            }
        }
    }

    val parser = KsoupHtmlParser(handler)
    parser.write(html)
    parser.end()

    return tags
}
