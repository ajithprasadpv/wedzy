package io.example.wedzy.data.repository

import io.example.wedzy.data.local.dao.AIRecommendationDao
import io.example.wedzy.data.model.AIRecommendation
import io.example.wedzy.data.model.UserAIPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIRecommendationRepository @Inject constructor(
    private val aiRecommendationDao: AIRecommendationDao
) {
    fun getActiveRecommendations(): Flow<List<AIRecommendation>> = 
        aiRecommendationDao.getActiveRecommendations()
    
    suspend fun getRecommendationById(recommendationId: Long): AIRecommendation? = 
        aiRecommendationDao.getRecommendationById(recommendationId)
    
    fun getRecommendationsByType(type: String): Flow<List<AIRecommendation>> = 
        aiRecommendationDao.getRecommendationsByType(type)
    
    fun getUnreadRecommendations(): Flow<List<AIRecommendation>> = 
        aiRecommendationDao.getUnreadRecommendations()
    
    fun getUnreadCount(): Flow<Int> = aiRecommendationDao.getUnreadCount()
    
    suspend fun insertRecommendation(recommendation: AIRecommendation): Long = 
        aiRecommendationDao.insertRecommendation(recommendation)
    
    suspend fun insertRecommendations(recommendations: List<AIRecommendation>) = 
        aiRecommendationDao.insertRecommendations(recommendations)
    
    suspend fun updateRecommendation(recommendation: AIRecommendation) = 
        aiRecommendationDao.updateRecommendation(recommendation)
    
    suspend fun deleteRecommendation(recommendation: AIRecommendation) = 
        aiRecommendationDao.deleteRecommendation(recommendation)
    
    suspend fun markAsRead(recommendationId: Long) = 
        aiRecommendationDao.markAsRead(recommendationId)
    
    suspend fun dismissRecommendation(recommendationId: Long) = 
        aiRecommendationDao.dismissRecommendation(recommendationId)
    
    suspend fun markAsActedUpon(recommendationId: Long) = 
        aiRecommendationDao.markAsActedUpon(recommendationId)
    
    suspend fun cleanupOldRecommendations(olderThanDays: Int = 30) {
        val olderThan = System.currentTimeMillis() - (olderThanDays * 24 * 60 * 60 * 1000L)
        aiRecommendationDao.cleanupOldRecommendations(olderThan)
    }
    
    suspend fun getUserAIPreferences(): UserAIPreferences? = 
        aiRecommendationDao.getUserAIPreferences()
    
    suspend fun saveUserAIPreferences(preferences: UserAIPreferences) = 
        aiRecommendationDao.insertUserAIPreferences(preferences)
    
    suspend fun updateUserAIPreferences(preferences: UserAIPreferences) = 
        aiRecommendationDao.updateUserAIPreferences(preferences)
}
