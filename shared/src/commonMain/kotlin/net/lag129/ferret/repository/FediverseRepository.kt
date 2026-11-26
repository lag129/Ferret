package net.lag129.ferret.repository

import net.lag129.ferret.model.Post
import net.lag129.ferret.model.Profile

interface FediverseRepository {

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

    suspend fun getAccountPosts(
        accountId: String,
        maxId: String? = null,
        sinceId: String? = null,
        limit: Int? = 20
    ): Result<List<Post>>

    suspend fun getMyCredential(): Result<Profile>

    suspend fun search(
        keyword: String,
        limit: Int? = 20
    ): Result<List<Post>>
}
