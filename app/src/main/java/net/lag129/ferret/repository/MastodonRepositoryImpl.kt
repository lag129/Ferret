package net.lag129.ferret.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.parameters
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.lag129.ferret.model.Post
import net.lag129.ferret.model.Profile
import net.lag129.ferret.model.converter.MastodonConverter.toPost
import net.lag129.ferret.model.converter.MastodonConverter.toProfile
import net.lag129.ferret.model.mastodon.Account
import net.lag129.ferret.model.mastodon.CredentialApplication
import net.lag129.ferret.model.mastodon.Search
import net.lag129.ferret.model.mastodon.Status
import net.lag129.ferret.model.mastodon.Token
import kotlin.coroutines.cancellation.CancellationException

class MastodonRepositoryImpl(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : FediverseRepository {

    override suspend fun getHomeTimeline(
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Post>> =
        getStatuses("/api/v1/timelines/home", maxId, sinceId, limit)

    override suspend fun getLocalTimeline(
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Post>> =
        getStatuses("/api/v1/timelines/public", maxId, sinceId, limit) {
            parameter("local", true)
        }

    override suspend fun getFederatedTimeline(
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Post>> =
        getStatuses("/api/v1/timelines/public", maxId, sinceId, limit)

    override suspend fun getAccountPosts(
        accountId: String,
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Post>> {
        return withContext(dispatcher) {
            runSuspendCatching {
                client.get("/api/v1/accounts/${accountId}/statuses") {
                    parameter("max_id", maxId)
                    parameter("since_id", sinceId)
                    parameter("limit", limit)
                }.body<List<Status>>()
            }
        }.map { statuses -> statuses.map { it.toPost() } }
    }

    override suspend fun getMyCredential(): Result<Profile> {
        return withContext(dispatcher) {
            runSuspendCatching {
                client.get("/api/v1/accounts/verify_credentials").body<Account>()
            }
        }.map { it.toProfile() }
    }

    override suspend fun search(
        keyword: String,
        limit: Int?
    ): Result<List<Post>> {
        return withContext(dispatcher) {
            runSuspendCatching {
                client.get("/api/v2/search") {
                    parameter("q", keyword)
                    parameter("limit", limit)
                }.body<Search>()
            }
        }.map { result -> result.statuses.map { it.toPost() } }
    }

    suspend fun registerClientApp(
        clientName: String,
        redirectUris: String,
        scopes: String,
        website: String? = null
    ): Result<CredentialApplication> {
        return withContext(dispatcher) {
            runSuspendCatching {
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
    }

    suspend fun obtainAccessToken(
        code: String,
        clientId: String,
        clientSecret: String,
        redirectUri: String
    ): Result<Token> {
        return withContext(dispatcher) {
            runSuspendCatching {
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

    private suspend fun getStatuses(
        url: String,
        maxId: String?,
        sinceId: String?,
        limit: Int?,
        extra: HttpRequestBuilder.() -> Unit = {}
    ): Result<List<Post>> {
        return withContext(dispatcher) {
            runSuspendCatching {
                client.get(url) {
                    parameter("max_id", maxId)
                    parameter("since_id", sinceId)
                    parameter("limit", limit)
                    extra()
                }.body<List<Status>>()
            }
        }.map { statuses -> statuses.map { it.toPost() } }
    }

    inline fun <T> runSuspendCatching(block: () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(e)
        }
}
