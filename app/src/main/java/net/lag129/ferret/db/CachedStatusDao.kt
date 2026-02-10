package net.lag129.ferret.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(statuses: List<CachedStatus>)

    @Delete
    suspend fun delete(cachedStatus: CachedStatus)

    @Query("SELECT * FROM cached_statuses WHERE timeline_type = :type ORDER BY cached_at DESC")
    suspend fun getCachedStatus(type: String): List<CachedStatus>
}
