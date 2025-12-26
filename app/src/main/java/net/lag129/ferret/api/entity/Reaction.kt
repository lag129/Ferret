package net.lag129.ferret.api.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Reaction(
    @SerialName("name")
    val name: String,

    @SerialName("count")
    val count: Int,

    @SerialName("me")
    val me: Boolean,

    @SerialName("url")
    val url: String? = null,

    @SerialName("static_url")
    val staticUrl: String? = null,

    @SerialName("domain")
    val domain: String? = null,

    @SerialName("width")
    val width: Int? = null,

    @SerialName("height")
    val height: Int? = null,

    @SerialName("account_ids")
    val accountIds: List<String>? = null
)
