package net.lag129.ferret.repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import net.lag129.ferret.model.misskey.Note
import kotlin.coroutines.cancellation.CancellationException

interface MisskeyRepository {

    suspend fun getHomeTimeline(
        maxId: String? = null,
        sinceId: String? = null,
        limit: Int? = 20
    ): Result<List<Note>>
}

class MisskeyRepositoryImpl(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : MisskeyRepository {

    override suspend fun getHomeTimeline(
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Note>> {
        return withContext(dispatcher) {
            runSuspendCatching {
                client.post("/api/notes/timeline") {
                    contentType(ContentType.Application.Json)
                    setBody(buildJsonObject {
                        maxId?.let { put("untilId", it) }
                        sinceId?.let { put("sinceId", it) }
                        limit?.let { put("limit", it) }
                    })
                }.body()
            }
        }
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
