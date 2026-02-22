package io.example.wedzy.data.repository

import io.example.wedzy.data.auth.UserSession
import io.example.wedzy.data.firebase.FirestoreSyncRepository
import io.example.wedzy.data.local.dao.WeddingProfileDao
import io.example.wedzy.data.model.WeddingProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeddingProfileRepository @Inject constructor(
    private val weddingProfileDao: WeddingProfileDao,
    private val userSession: UserSession,
    private val sync: FirestoreSyncRepository
) {
    fun getProfile(): Flow<WeddingProfile?> {
        val userId = userSession.getCurrentUserId()
        return weddingProfileDao.getProfile(userId)
    }
    
    suspend fun getProfileOnce(): WeddingProfile? {
        val userId = userSession.getCurrentUserId()
        return weddingProfileDao.getProfileOnce(userId)
    }
    
    suspend fun saveProfile(profile: WeddingProfile): Long {
        val userId = userSession.getCurrentUserId()
        val withUser = profile.copy(userId = userId)
        val id = weddingProfileDao.insertProfile(withUser)
        sync.syncProfileToCloud(withUser.copy(id = id))
        return id
    }
    
    suspend fun updateProfile(profile: WeddingProfile) {
        val userId = userSession.getCurrentUserId()
        val updated = profile.copy(userId = userId, updatedAt = System.currentTimeMillis())
        weddingProfileDao.updateProfile(updated)
        sync.syncProfileToCloud(updated)
    }
    
    suspend fun deleteProfile(profile: WeddingProfile) {
        weddingProfileDao.deleteProfile(profile)
    }
    
    suspend fun hasProfile(): Boolean {
        val userId = userSession.getCurrentUserId()
        return weddingProfileDao.getProfileOnce(userId) != null
    }
    
    suspend fun clearUserData() {
        val userId = userSession.getCurrentUserId()
        if (userId.isNotEmpty()) {
            weddingProfileDao.deleteAll(userId)
        }
    }
}
