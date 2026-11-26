package net.lag129.ferret.db

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "cached_statuses", primaryKeys = ["status_id"])
data class CachedPost(

    @ColumnInfo(name = "status_id")
    val postId: String,

    @ColumnInfo(name = "timeline_type")
    val timelineType: String,

    @ColumnInfo(name = "status_json")
    val postJson: String,

    @ColumnInfo(name = "order_index")
    val orderIndex: Int,
)
