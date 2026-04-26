package net.lag129.ferret.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomEmoji(
    @SerialName("shortcode")
    val shortcode: String,

    @SerialName("url")
    val url: String,

    @SerialName("static_url")
    val staticUrl: String,

    @SerialName("visible_in_picker")
    val visibleInPicker: Boolean,

    @SerialName("width")
    val width: Int? = null,

    @SerialName("height")
    val height: Int? = null,

    @SerialName("category")
    val category: String? = null
)
