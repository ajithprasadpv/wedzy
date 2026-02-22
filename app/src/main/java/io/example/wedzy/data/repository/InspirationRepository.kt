package io.example.wedzy.data.repository

import io.example.wedzy.data.auth.UserSession
import io.example.wedzy.data.local.dao.InspirationDao
import io.example.wedzy.data.model.Inspiration
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InspirationRepository @Inject constructor(
    private val inspirationDao: InspirationDao,
    private val userSession: UserSession
) {
    fun getAllInspirations(): Flow<List<Inspiration>> {
        val userId = userSession.getCurrentUserId()
        return inspirationDao.getAllInspirations(userId)
    }
    
    suspend fun getInspirationById(inspirationId: Long): Inspiration? = 
        inspirationDao.getInspirationById(inspirationId)
    
    fun getInspirationsByCategory(category: String): Flow<List<Inspiration>> {
        val userId = userSession.getCurrentUserId()
        return inspirationDao.getInspirationsByCategory(userId, category)
    }
    
    fun getFavoriteInspirations(): Flow<List<Inspiration>> {
        val userId = userSession.getCurrentUserId()
        return inspirationDao.getFavoriteInspirations(userId)
    }
    
    fun getInspirationCount(): Flow<Int> {
        val userId = userSession.getCurrentUserId()
        return inspirationDao.getInspirationCount(userId)
    }
    
    suspend fun insertInspiration(inspiration: Inspiration): Long {
        val userId = userSession.getCurrentUserId()
        return inspirationDao.insertInspiration(inspiration.copy(userId = userId))
    }
    
    suspend fun updateInspiration(inspiration: Inspiration) {
        val userId = userSession.getCurrentUserId()
        inspirationDao.updateInspiration(inspiration.copy(userId = userId))
    }
    
    suspend fun deleteInspiration(inspiration: Inspiration) = inspirationDao.deleteInspiration(inspiration)
    
    suspend fun toggleFavorite(inspirationId: Long, isFavorite: Boolean) = 
        inspirationDao.updateFavoriteStatus(inspirationId, isFavorite)
}
