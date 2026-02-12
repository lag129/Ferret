package net.lag129.ferret.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(statuses: List<CachedStatus>)

    @Query("DELETE FROM cached_statuses WHERE timeline_type = :type")
    suspend fun clearTimeline(type: String)

    @Query("SELECT * FROM cached_statuses WHERE timeline_type = :type ORDER BY order_index ASC")
    suspend fun getCachedStatus(type: String): List<CachedStatus>
}
