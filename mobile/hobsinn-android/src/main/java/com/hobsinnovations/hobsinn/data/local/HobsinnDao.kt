package com.hobsinnovations.hobsinn.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HobsinnDao {
    @Insert
    suspend fun insertEvent(event: CachedEvent)

    @Query("SELECT * FROM cached_events WHERE isSynced = 0")
    fun getUnsyncedEvents(): Flow<List<CachedEvent>>

    @Query("UPDATE cached_events SET isSynced = 1 WHERE id = :eventId")
    suspend fun markAsSynced(eventId: Int)
}
