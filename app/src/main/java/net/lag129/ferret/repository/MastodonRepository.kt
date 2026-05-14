package net.lag129.ferret.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.parameters
import net.lag129.ferret.model.Account
import net.lag129.ferret.model.CredentialApplication
import net.lag129.ferret.model.Status
import net.lag129.ferret.model.Token

interface MastodonRepository {

    suspend fun getHomeTimeline(
        maxId: String? = null,
        sinceId: String? = null,
        limit: Int? = 20
    ): Result<List<Status>>

    suspend fun getLocalTimeline(
        maxId: String? = null,
        sinceId: String? = null,
        limit: Int? = 20
    ): Result<List<Status>>

    suspend fun getFederatedTimeline(
        maxId: String? = null,
        sinceId: String? = null,
        limit: Int? = 20
    ): Result<List<Status>>

    suspend fun getAccountStatuses(
        accountId: String,
        maxId: String? = null,
        sinceId: String? = null,
        limit: Int? = 20
    ): Result<List<Status>>

    suspend fun getMyCredential(): Result<Account>

    suspend fun registerClientApp(
        clientName: String,
        redirectUris: String,
        scopes: String,
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

    override suspend fun getLocalTimeline(
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Status>> {
        return runCatching {
            client.get("/api/v1/timelines/public") {
                parameter("local", true)
                parameter("max_id", maxId)
                parameter("since_id", sinceId)
                parameter("limit", limit)
            }.body()
        }
    }

    override suspend fun getFederatedTimeline(
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Status>> {
        return runCatching {
            client.get("/api/v1/timelines/public") {
                parameter("max_id", maxId)
                parameter("since_id", sinceId)
                parameter("limit", limit)
            }.body()
        }
    }

    override suspend fun getAccountStatuses(
        accountId: String,
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Status>> {
        return runCatching {
            client.get("/api/v1/accounts/${accountId}/statuses") {
                parameter("max_id", maxId)
                parameter("since_id", sinceId)
                parameter("limit", limit)
            }.body()
        }
    }

    override suspend fun getMyCredential(): Result<Account> {
        return runCatching {
            client.get("/api/v1/accounts/verify_credentials").body()
        }
    }

    override suspend fun registerClientApp(
        clientName: String,
        redirectUris: String,
        scopes: String,
        website: String?
    ): Result<CredentialApplication> {
        return runCatching {
            client.submitForm(
                url = "/api/v1/apps",
                formParameters = parameters {
                    append("client_name", clientName)
                    append("redirect_uris", redirectUris)
                    append("scopes", scopes)
                    website?.let { append("website", it) }
                }
            ).body()
        }
    }

    override suspend fun obtainAccessToken(
        code: String,
        clientId: String,
        clientSecret: String,
        redirectUri: String
    ): Result<Token> {
        return runCatching {
            client.submitForm(
                url = "/oauth/token",
                formParameters = parameters {
                    append("grant_type", "authorization_code")
                    append("code", code)
                    append("client_id", clientId)
                    append("client_secret", clientSecret)
                    append("redirect_uri", redirectUri)
                }
            ).body()
        }
    }
}
