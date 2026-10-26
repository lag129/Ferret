package net.lag129.ferret.model.misskey

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MiAuth(
    @SerialName("ok")
    val ok: Boolean,

    @SerialName("token")
    val token: String? = null,
)
