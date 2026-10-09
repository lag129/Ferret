package net.lag129.ferret.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ShallowTag(
    @SerialName("name")
    val name: String,

    @SerialName("url")
    val url: String,
)
