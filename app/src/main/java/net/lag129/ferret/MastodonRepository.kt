package net.lag129.ferret

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import net.lag129.ferret.api.entity.Status

interface MastodonRepository {

    suspend fun getHomeTimeline(
        id: String,
        maxId: String? = null,
        sinceId: String? = null,
        limit: Int? = 20
    ): Result<List<Status>>
}

class MastodonRepositoryImpl(private val client: HttpClient) : MastodonRepository {

    override suspend fun getHomeTimeline(
        id: String,
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Status>> {
        return runCatching {
            client.get("/api/v1/accounts/$id/statuses") {
                parameter("max_id", maxId)
                parameter("since_id", sinceId)
                parameter("limit", limit)
            }.body()
        }
    }
}
