package net.lag129.ferret.api.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Application(
    @SerialName("name")
    val name: String,

    @SerialName("website")
    val website: String? = null
)
