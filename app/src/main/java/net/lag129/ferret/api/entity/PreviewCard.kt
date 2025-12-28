package net.lag129.ferret.api.entity

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class PreviewCard(

    @SerialName("url")
    val url: String,

    @SerialName("title")
    val title: String,

    @SerialName("description")
    val description: String,

    @SerialName("type")
    val type: String,

    @SerialName("authors")
    val authors: List<PreviewCardAuthor>? = null,

    @SerialName("author_name")
    val authorName: String,

    @SerialName("author_url")
    val authorUrl: String,

    @SerialName("provider_name")
    val providerName: String,

    @SerialName("provider_url")
    val providerUrl: String,

    @SerialName("html")
    val html: String,

    @SerialName("width")
    val width: Int,

    @SerialName("height")
    val height: Int,

    @SerialName("image")
    val image: String? = null,

    @SerialName("embed_url")
    val embedUrl: String,

    @SerialName("blurhash")
    val blurhash: String? = null
)

@Serializable
data class PreviewCardAuthor(

    @SerialName("name")
    val name: String,

    @SerialName("url")
    val url: String,

    @SerialName("account")
    val account: Account?
)
