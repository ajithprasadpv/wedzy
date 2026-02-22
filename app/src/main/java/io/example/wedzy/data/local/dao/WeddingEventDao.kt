package io.example.wedzy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.example.wedzy.data.model.WeddingEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface WeddingEventDao {
    @Query("SELECT * FROM wedding_events WHERE userId = :userId ORDER BY startDateTime ASC")
    fun getAllEvents(userId: String): Flow<List<WeddingEvent>>
    
    @Query("SELECT * FROM wedding_events WHERE id = :eventId")
    suspend fun getEventById(eventId: Long): WeddingEvent?
    
    @Query("SELECT * FROM wedding_events WHERE userId = :userId AND startDateTime >= :startTime AND startDateTime <= :endTime ORDER BY startDateTime ASC")
    fun getEventsBetween(userId: String, startTime: Long, endTime: Long): Flow<List<WeddingEvent>>
    
    @Query("SELECT * FROM wedding_events WHERE userId = :userId AND startDateTime >= :today ORDER BY startDateTime ASC LIMIT :limit")
    fun getUpcomingEvents(userId: String, today: Long, limit: Int = 5): Flow<List<WeddingEvent>>
    
    @Query("SELECT * FROM wedding_events WHERE userId = :userId AND eventType = :eventType ORDER BY startDateTime ASC")
    fun getEventsByType(userId: String, eventType: String): Flow<List<WeddingEvent>>
    
    @Query("SELECT COUNT(*) FROM wedding_events WHERE userId = :userId")
    fun getEventCount(userId: String): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: WeddingEvent): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<WeddingEvent>)
    
    @Update
    suspend fun updateEvent(event: WeddingEvent)
    
    @Delete
    suspend fun deleteEvent(event: WeddingEvent)
    
    @Query("DELETE FROM wedding_events WHERE id = :eventId")
    suspend fun deleteEventById(eventId: Long)
}
