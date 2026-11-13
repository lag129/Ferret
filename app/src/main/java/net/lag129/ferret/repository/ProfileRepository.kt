package net.lag129.ferret.repository

import net.lag129.ferret.ui.model.Post
import net.lag129.ferret.ui.model.converter.MastodonConverter.toPost
import net.lag129.ferret.ui.model.converter.MisskeyConverter.toPost

interface ProfileRepository {

    suspend fun getProfile(
        accountId: String,
        maxId: String? = null,
        sinceId: String? = null,
        limit: Int? = 20
    ): Result<List<Post>>
}

class MastodonProfileRepositoryImpl(
    private val mastodonRepository: MastodonRepository
) : ProfileRepository {

    override suspend fun getProfile(
        accountId: String,
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Post>> {
        return mastodonRepository.getAccountStatuses(accountId, maxId, sinceId, limit)
            .map { statuses -> statuses.map { it.toPost() } }
    }
}

class MisskeyProfileRepositoryImpl(
    private val misskeyRepository: MisskeyRepository
) : ProfileRepository {

    override suspend fun getProfile(
        accountId: String,
        maxId: String?,
        sinceId: String?,
        limit: Int?
    ): Result<List<Post>> {
        return misskeyRepository.getUserNotes(accountId, sinceId, maxId, limit)
            .map { note -> note.map { it.toPost() } }
    }
}
