package net.lag129.ferret.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CachedStatus::class], version = 1)
abstract class RoomDatabase : RoomDatabase() {
    abstract fun cachedStatusDao(): CachedStatusDao
}
