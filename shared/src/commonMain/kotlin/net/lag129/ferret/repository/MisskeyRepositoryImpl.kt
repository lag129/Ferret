package net.lag129.ferret.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import net.lag129.ferret.model.Post
import net.lag129.ferret.model.Profile
import net.lag129.ferret.model.converter.MisskeyConverter.toPost
import net.lag129.ferret.model.misskey.MiAuth
import net.lag129.ferret.model.misskey.Note
import kotlin.coroutines.cancellation.CancellationException

class MisskeyRepositoryImpl(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : FediverseRepository {

    override suspend fun getHomeTimeline(
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Post>> =
        getNotes("/api/notes/timeline", sinceId, maxId, limit)

    override suspend fun getLocalTimeline(
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Post>> =
        getNotes("/api/notes/local-timeline", sinceId, maxId, limit)

    override suspend fun getFederatedTimeline(
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Post>> =
        getNotes("/api/notes/global-timeline", sinceId, maxId, limit)

    override suspend fun getAccountPosts(
        accountId: String,
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Post>> {
        return withContext(dispatcher) {
            runSuspendCatching {
                client.post("/api/users/notes") {
                    contentType(ContentType.Application.Json)
                    setBody(buildJsonObject {
                        put("userId", accountId)
                        sinceId?.let { put("sinceId", it) }
                        maxId?.let { put("untilId", it) }
                        limit?.let { put("limit", it) }
                    })
                }.body<List<Note>>()
            }
        }.map { notes -> notes.map { it.toPost() } }
    }

    override suspend fun getMyCredential(): Result<Profile> {
        TODO("Not yet implemented")
    }

    override suspend fun search(keyword: String, limit: Int?): Result<List<Post>> {
        TODO("Not yet implemented")
    }

    suspend fun checkMiAuth(session: String): Result<MiAuth> {
        return withContext(dispatcher) {
            runSuspendCatching {
                client.post("/api/miauth/$session/check") {
                    contentType(ContentType.Application.Json)
                    setBody(buildJsonObject { })
                }.body()
            }
        }
    }

    private suspend fun getNotes(
        url: String,
        sinceId: String?,
        untilId: String?,
        limit: Int?
    ): Result<List<Post>> {
        return withContext(dispatcher) {
            runSuspendCatching {
                client.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(buildJsonObject {
                        sinceId?.let { put("sinceId", it) }
                        untilId?.let { put("untilId", it) }
                        limit?.let { put("limit", it) }
                    })
                }.body<List<Note>>()
            }
        }.map { notes -> notes.map { it.toPost() } }
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
