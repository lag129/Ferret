package net.lag129.ferret

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser

@Composable
fun htmlToAnnotatedString(
    html: String,
    urlSpanStyle: SpanStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        textDecoration = TextDecoration.Underline
    )
): AnnotatedString {
    return parseHtmlToAnnotatedString(html, urlSpanStyle)
}

fun parseHtmlToAnnotatedString(
    html: String,
    urlSpanStyle: SpanStyle = SpanStyle()
): AnnotatedString {
    val builder = Builder()
    val linkStack = mutableListOf<Boolean>()

    val handler = object : KsoupHtmlHandler {
        override fun onOpenTag(
            name: String,
            attributes: Map<String, String>,
            isImplied: Boolean
        ) {
            when (name.lowercase()) {
                "p" -> {
                    if (builder.length > 0) {
                        builder.pushStyle(ParagraphStyle(lineHeight = 16.sp))
                        builder.pop()
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
            }
        }

        override fun onText(text: String) {
            if (text.isNotBlank()) {
                builder.append(text)
            }
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
            }
        }
    }

    val parser = KsoupHtmlParser(handler)
    parser.write(html)
    parser.end()

    return builder.toAnnotatedString()
}
