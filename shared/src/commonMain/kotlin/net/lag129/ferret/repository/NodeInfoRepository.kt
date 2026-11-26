package net.lag129.ferret.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.lag129.ferret.model.NodeInfo
import net.lag129.ferret.model.NodeInfoLinks
import kotlin.coroutines.cancellation.CancellationException

interface NodeInfoRepository {

    suspend fun getSoftwareName(): Result<String>
}

class NodeInfoRepositoryImpl(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : NodeInfoRepository {

    override suspend fun getSoftwareName(): Result<String> {
        return withContext(dispatcher) {
            runSuspendCatching {
                val nodeInfoLinks: NodeInfoLinks = client.get("/.well-known/nodeinfo").body()
                val nodeInfo: NodeInfo = client.get(nodeInfoLinks.links.first().href).body()
                nodeInfo.software.name
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
