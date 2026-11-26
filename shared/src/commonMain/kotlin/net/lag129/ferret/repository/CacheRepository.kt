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
    private val cachedPostDao: CachedPostDao,
) : CacheRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getPosts(timelineType: String): List<Post> {
        val cached = cachedPostDao.getCachedPosts(timelineType)
        return withContext(Dispatchers.Default) {
            cached.map { json.decodeFromString<Post>(it.postJson) }
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

        cachedPostDao.clearTimeline(timelineType)
        cachedPostDao.insertAll(cachedPosts)
    }
}
