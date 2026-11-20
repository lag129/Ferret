package net.lag129.ferret.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser

private enum class SpanState {
    INVISIBLE, ELLIPSIS, NORMAL
}

fun parseHtmlToAnnotatedString(
    html: String,
    urlSpanStyle: SpanStyle = SpanStyle()
): AnnotatedString {
    val builder = Builder()
    val linkStack = mutableListOf<Boolean>()
    val spanStack = mutableListOf<SpanState>()

    val handler = object : KsoupHtmlHandler {
        override fun onOpenTag(
            name: String,
            attributes: Map<String, String>,
            isImplied: Boolean
        ) {
            when (name.lowercase()) {
                "p" -> {
                    if (builder.length > 0) {
                        builder.append("\n")
                    }
                }

                "br" -> builder.append('\n')
                "a" -> {
                    val href = attributes["href"]
                    if (!href.isNullOrBlank()) {
                        builder.pushLink(LinkAnnotation.Url(href))
                        builder.pushStyle(urlSpanStyle)
                        linkStack.add(true)
                    } else {
                        linkStack.add(false)
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

            builder.append(text)
        }

        override fun onCloseTag(name: String, isImplied: Boolean) {
            when (name.lowercase()) {
                "a" -> {
                    if (linkStack.isNotEmpty()) {
                        val hadLink = linkStack.removeAt(linkStack.size - 1)
                        if (hadLink) {
                            builder.pop()
                            builder.pop()
                        }
                    }
                }

                "span" -> {
                    if (spanStack.isNotEmpty()) {
                        val state = spanStack.removeAt(spanStack.size - 1)
                        if (state == SpanState.ELLIPSIS) {
                            builder.append("...")
                        }
                    }
                }
            }
        }
    }

    val parser = KsoupHtmlParser(handler)
    parser.write(html)
    parser.end()

    return builder.toAnnotatedString()
}
