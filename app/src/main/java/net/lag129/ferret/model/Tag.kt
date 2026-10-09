package net.lag129.ferret.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Tag(
    @SerialName("id")
    val id: String? = null,

    @SerialName("name")
    val name: String,

    @SerialName("url")
    val url: String,

    @SerialName("history")
    val history: List<Hash>,

    @SerialName("following")
    val following: Boolean? = null,

    @SerialName("featuring")
    val featuring: Boolean? = null,
)

@Immutable
@Serializable
data class Hash(
    @SerialName("day")
    val day: String,

    @SerialName("uses")
    val uses: String,

    @SerialName("accounts")
    val accounts: String,
)
