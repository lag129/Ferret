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

@Serializable
data class CredentialApplication(

    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("website")
    val website: String? = null,

    @SerialName("scopes")
    val scopes: List<String>? = null,

    @SerialName("redirect_uris")
    val redirectUris: List<String>? = null,

    @SerialName("client_id")
    val clientId: String,

    @SerialName("client_secret")
    val clientSecret: String,

    @SerialName("client_secret_expires_at")
    val clientSecretExpiresAt: Int? = null
)
