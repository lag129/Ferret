package net.lag129.ferret.repository

import net.lag129.ferret.ui.model.Post

interface TimelineRepository {

    suspend fun getHomeTimeline(
        maxId: String? = null,
        sinceId: String? = null,
        limit: Int? = 20
    ): Result<List<Post>>

    suspend fun getLocalTimeline(
        maxId: String? = null,
        sinceId: String? = null,
        limit: Int? = 20
    ): Result<List<Post>>

    suspend fun getFederatedTimeline(
        maxId: String? = null,
        sinceId: String? = null,
        limit: Int? = 20
    ): Result<List<Post>>
}

class MastodonTimelineRepositoryImpl(
    private val mastodonRepository: MastodonRepository
) : TimelineRepository {

    override suspend fun getHomeTimeline(
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Post>> {
        return mastodonRepository.getHomeTimeline(maxId, sinceId, limit)
            .map { statuses -> statuses.map { it.toPost() } }
    }

    override suspend fun getLocalTimeline(
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Post>> {
        return mastodonRepository.getLocalTimeline(maxId, sinceId, limit)
            .map { statuses -> statuses.map { it.toPost() } }
    }

    override suspend fun getFederatedTimeline(
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Post>> {
        return mastodonRepository.getFederatedTimeline(maxId, sinceId, limit)
            .map { statuses -> statuses.map { it.toPost() } }
    }
}

class MisskeyTimelineRepositoryImpl(
    private val misskeyRepository: MisskeyRepository
) : TimelineRepository {

    override suspend fun getHomeTimeline(
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Post>> {
        return misskeyRepository.getHomeTimeline(maxId, sinceId, limit)
            .map { note -> note.map { it.toPost() } }
    }

    override suspend fun getLocalTimeline(
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Post>> {
        return misskeyRepository.getLocalTimeline(maxId, sinceId, limit)
            .map { statuses -> statuses.map { it.toPost() } }
    }

    override suspend fun getFederatedTimeline(
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Post>> {
        return misskeyRepository.getFederatedTimeline(maxId, sinceId, limit)
            .map { statuses -> statuses.map { it.toPost() } }
    }
}
