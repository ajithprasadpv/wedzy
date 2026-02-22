package io.example.wedzy.data.repository

import io.example.wedzy.data.auth.UserSession
import io.example.wedzy.data.firebase.FirestoreSyncRepository
import io.example.wedzy.data.local.dao.WeddingEventDao
import io.example.wedzy.data.model.WeddingEvent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeddingEventRepository @Inject constructor(
    private val weddingEventDao: WeddingEventDao,
    private val userSession: UserSession,
    private val sync: FirestoreSyncRepository
) {
    fun getAllEvents(): Flow<List<WeddingEvent>> {
        val userId = userSession.getCurrentUserId()
        return weddingEventDao.getAllEvents(userId)
    }
    
    suspend fun getEventById(eventId: Long): WeddingEvent? = weddingEventDao.getEventById(eventId)
    
    fun getEventsBetween(startTime: Long, endTime: Long): Flow<List<WeddingEvent>> {
        val userId = userSession.getCurrentUserId()
        return weddingEventDao.getEventsBetween(userId, startTime, endTime)
    }
    
    fun getUpcomingEvents(limit: Int = 5): Flow<List<WeddingEvent>> {
        val userId = userSession.getCurrentUserId()
        return weddingEventDao.getUpcomingEvents(userId, System.currentTimeMillis(), limit)
    }
    
    fun getEventCount(): Flow<Int> {
        val userId = userSession.getCurrentUserId()
        return weddingEventDao.getEventCount(userId)
    }
    
    suspend fun insertEvent(event: WeddingEvent): Long {
        val userId = userSession.getCurrentUserId()
        val withUser = event.copy(userId = userId)
        val id = weddingEventDao.insertEvent(withUser)
        sync.syncEventToCloud(withUser.copy(id = id))
        return id
    }
    
    suspend fun updateEvent(event: WeddingEvent) {
        val userId = userSession.getCurrentUserId()
        val withUser = event.copy(userId = userId)
        weddingEventDao.updateEvent(withUser)
        sync.syncEventToCloud(withUser)
    }
    
    suspend fun deleteEvent(event: WeddingEvent) {
        weddingEventDao.deleteEvent(event)
        sync.deleteEventFromCloud(event.id)
    }
}
