package net.lag129.ferret.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NodeInfoLinks(
    @SerialName("links")
    val links: List<Link>,
) {
    @Serializable
    data class Link(
        @SerialName("rel")
        val rel: String,

        @SerialName("href")
        val href: String,
    )
}

@Serializable
data class NodeInfo(
    @SerialName("software")
    val software: Software,
) {
    @Serializable
    data class Software(
        @SerialName("name")
        val name: String,
    )
}
