package net.lag129.ferret

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import net.lag129.ferret.api.entity.CredentialApplication
import net.lag129.ferret.api.entity.Status
import net.lag129.ferret.api.entity.Token

interface MastodonRepository {

    suspend fun getHomeTimeline(
        maxId: String? = null,
        sinceId: String? = null,
        limit: Int? = 20
    ): Result<List<Status>>

    suspend fun registerClientApp(
        clientName: String,
        redirectUris: String,
        scopes: String? = null,
        website: String? = null
    ): Result<CredentialApplication>

    suspend fun obtainAccessToken(
        code: String,
        clientId: String,
        clientSecret: String,
        redirectUri: String
    ): Result<Token>
}

class MastodonRepositoryImpl(private val client: HttpClient) : MastodonRepository {

    override suspend fun getHomeTimeline(
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Status>> {
        return runCatching {
            client.get("/api/v1/timelines/home") {
                parameter("max_id", maxId)
                parameter("since_id", sinceId)
                parameter("limit", limit)
            }.body()
        }
    }

    override suspend fun registerClientApp(
        clientName: String,
        redirectUris: String,
        scopes: String?,
        website: String?
    ): Result<CredentialApplication> {
        return runCatching {
            client.get("/api/v1/apps") {
                parameter("client_name", clientName)
                parameter("redirect_uris", redirectUris)
                scopes?.let { parameter("scopes", it) }
                website?.let { parameter("website", it) }
            }.body()
        }
    }

    override suspend fun obtainAccessToken(
        code: String,
        clientId: String,
        clientSecret: String,
        redirectUri: String
    ): Result<Token> {
        return runCatching {
            client.get("/oauth/token") {
                parameter("grant_type", "authorization_code")
                parameter("code", code)
                parameter("client_id", clientId)
                parameter("client_secret", clientSecret)
                parameter("redirect_uri", redirectUri)
            }.body()
        }
    }
}
