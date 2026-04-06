package com.hobsinnovations.hobsinn.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_events")
data class CachedEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventType: String,
    val payloadJson: String,
    val isSynced: Boolean = false
)
