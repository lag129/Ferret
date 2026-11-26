package net.lag129.ferret.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [CachedPost::class], version = 2)
@ConstructedBy(FerretDatabaseConstructor::class)
abstract class FerretDatabase : RoomDatabase() {
    abstract fun cachedPostDao(): CachedPostDao
}

@Suppress("KotlinNoActualForExpect")
expect object FerretDatabaseConstructor : RoomDatabaseConstructor<FerretDatabase> {
    override fun initialize(): FerretDatabase
}
