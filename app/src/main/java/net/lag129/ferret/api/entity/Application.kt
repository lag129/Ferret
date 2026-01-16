package net.lag129.ferret.api.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Application {
    abstract val id: String
    abstract val name: String
    abstract val website: String?
    abstract val scopes: List<String>
    abstract val redirectUris: List<String>
}

@Serializable
data class CredentialApplication(

    @SerialName("id")
    override val id: String,

    @SerialName("name")
    override val name: String,

    @SerialName("website")
    override val website: String? = null,

    @SerialName("scopes")
    override val scopes: List<String>,

    @SerialName("redirect_uris")
    override val redirectUris: List<String>,

    @SerialName("client_id")
    val clientId: String,

    @SerialName("client_secret")
    val clientSecret: String,

    @SerialName("client_secret_expires_at")
    val clientSecretExpiresAt: Int
) : Application()
