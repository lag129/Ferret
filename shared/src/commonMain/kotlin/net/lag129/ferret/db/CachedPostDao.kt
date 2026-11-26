package net.lag129.ferret.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface CachedPostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<CachedPost>)

    @Query("DELETE FROM cached_statuses WHERE timeline_type = :type")
    suspend fun clearTimeline(type: String)

    @Query("SELECT * FROM cached_statuses WHERE timeline_type = :type ORDER BY order_index ASC")
    suspend fun getCachedPosts(type: String): List<CachedPost>

    @Transaction
    suspend fun replaceTimeline(type: String, posts: List<CachedPost>) {
        clearTimeline(type)
        insertAll(posts)
    }
}
