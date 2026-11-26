package net.lag129.ferret.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import net.lag129.ferret.db.CachedPost
import net.lag129.ferret.db.CachedPostDao
import net.lag129.ferret.model.Post

interface CacheRepository {

    suspend fun getPosts(timelineType: String): List<Post>

    suspend fun savePosts(timelineType: String, posts: List<Post>)
}

internal class CacheRepositoryImpl(
    private val dao: CachedPostDao,
) : CacheRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getPosts(timelineType: String): List<Post> {
        val cached = dao.getCachedPosts(timelineType)
        return try {
            withContext(Dispatchers.Default) {
                cached.map { json.decodeFromString<Post>(it.postJson) }
            }
        } catch (_: IllegalArgumentException) {
            dao.clearTimeline(timelineType)
            emptyList()
        }
    }

    override suspend fun savePosts(timelineType: String, posts: List<Post>) {
        val cachedPosts = withContext(Dispatchers.Default) {
            posts.mapIndexed { index, post ->
                CachedPost(
                    postId = post.id,
                    timelineType = timelineType,
                    postJson = json.encodeToString(post),
                    orderIndex = index,
                )
            }
        }

        dao.replaceTimeline(timelineType, cachedPosts)
    }
}
