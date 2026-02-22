package io.example.wedzy.data.repository

import io.example.wedzy.data.auth.UserSession
import io.example.wedzy.data.firebase.FirestoreSyncRepository
import io.example.wedzy.data.local.dao.GuestDao
import io.example.wedzy.data.model.Guest
import io.example.wedzy.data.model.GuestSide
import io.example.wedzy.data.model.RsvpStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GuestRepository @Inject constructor(
    private val guestDao: GuestDao,
    private val userSession: UserSession,
    private val sync: FirestoreSyncRepository
) {
    fun getAllGuests(): Flow<List<Guest>> {
        val userId = userSession.getCurrentUserId()
        return guestDao.getAllGuests(userId)
    }
    
    fun getGuestsByRsvpStatus(status: RsvpStatus): Flow<List<Guest>> {
        val userId = userSession.getCurrentUserId()
        return guestDao.getGuestsByRsvpStatus(userId, status)
    }
    
    fun getGuestsBySide(side: GuestSide): Flow<List<Guest>> {
        val userId = userSession.getCurrentUserId()
        return guestDao.getGuestsBySide(userId, side)
    }
    
    suspend fun getGuestById(id: Long): Guest? = guestDao.getGuestById(id)
    
    fun getTotalGuestCount(): Flow<Int> {
        val userId = userSession.getCurrentUserId()
        return guestDao.getTotalGuestCount(userId)
    }
    
    fun getGuestCountByStatus(status: RsvpStatus): Flow<Int> {
        val userId = userSession.getCurrentUserId()
        return guestDao.getGuestCountByStatus(userId, status)
    }
    
    fun getConfirmedAttendeeCount(): Flow<Int> {
        val userId = userSession.getCurrentUserId()
        return combine(
            guestDao.getConfirmedGuestCount(userId),
            guestDao.getConfirmedPlusOneCount(userId)
        ) { guests, plusOnes -> guests + plusOnes }
    }
    
    fun getGuestsByTable(tableNumber: Int): Flow<List<Guest>> {
        val userId = userSession.getCurrentUserId()
        return guestDao.getGuestsByTable(userId, tableNumber)
    }
    
    fun getGuestsWithGifts(): Flow<List<Guest>> {
        val userId = userSession.getCurrentUserId()
        return guestDao.getGuestsWithGifts(userId)
    }
    
    fun getGuestsNeedingThankYou(): Flow<List<Guest>> {
        val userId = userSession.getCurrentUserId()
        return guestDao.getGuestsNeedingThankYou(userId)
    }
    
    suspend fun insertGuest(guest: Guest): Long {
        val userId = userSession.getCurrentUserId()
        val withUser = guest.copy(userId = userId)
        val id = guestDao.insertGuest(withUser)
        sync.syncGuestToCloud(withUser.copy(id = id))
        return id
    }
    
    suspend fun insertGuests(guests: List<Guest>) {
        val userId = userSession.getCurrentUserId()
        val guestsWithUserId = guests.map { it.copy(userId = userId) }
        guestDao.insertGuests(guestsWithUserId)
        guestsWithUserId.forEach { sync.syncGuestToCloud(it) }
    }
    
    suspend fun updateGuest(guest: Guest) {
        val userId = userSession.getCurrentUserId()
        val withUser = guest.copy(userId = userId)
        guestDao.updateGuest(withUser)
        sync.syncGuestToCloud(withUser)
    }
    
    suspend fun updateRsvpStatus(guest: Guest, status: RsvpStatus) {
        val userId = userSession.getCurrentUserId()
        val updated = guest.copy(userId = userId, rsvpStatus = status)
        guestDao.updateGuest(updated)
        sync.syncGuestToCloud(updated)
    }
    
    suspend fun deleteGuest(guest: Guest) {
        guestDao.deleteGuest(guest)
        sync.deleteGuestFromCloud(guest.id)
    }
    
    suspend fun deleteGuestById(id: Long) {
        guestDao.deleteGuestById(id)
        sync.deleteGuestFromCloud(id)
    }
    
    suspend fun clearUserData() {
        val userId = userSession.getCurrentUserId()
        if (userId.isNotEmpty()) {
            guestDao.deleteAllGuests(userId)
        }
    }
}
