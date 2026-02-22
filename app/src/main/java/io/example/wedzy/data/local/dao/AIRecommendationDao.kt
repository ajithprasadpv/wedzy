package io.example.wedzy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.example.wedzy.data.model.AIRecommendation
import io.example.wedzy.data.model.UserAIPreferences
import kotlinx.coroutines.flow.Flow

@Dao
interface AIRecommendationDao {
    @Query("SELECT * FROM ai_recommendations WHERE isDismissed = 0 ORDER BY priority DESC, createdAt DESC")
    fun getActiveRecommendations(): Flow<List<AIRecommendation>>
    
    @Query("SELECT * FROM ai_recommendations WHERE id = :recommendationId")
    suspend fun getRecommendationById(recommendationId: Long): AIRecommendation?
    
    @Query("SELECT * FROM ai_recommendations WHERE type = :type AND isDismissed = 0 ORDER BY createdAt DESC")
    fun getRecommendationsByType(type: String): Flow<List<AIRecommendation>>
    
    @Query("SELECT * FROM ai_recommendations WHERE isRead = 0 ORDER BY priority DESC")
    fun getUnreadRecommendations(): Flow<List<AIRecommendation>>
    
    @Query("SELECT COUNT(*) FROM ai_recommendations WHERE isRead = 0 AND isDismissed = 0")
    fun getUnreadCount(): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendation(recommendation: AIRecommendation): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendations(recommendations: List<AIRecommendation>)
    
    @Update
    suspend fun updateRecommendation(recommendation: AIRecommendation)
    
    @Delete
    suspend fun deleteRecommendation(recommendation: AIRecommendation)
    
    @Query("UPDATE ai_recommendations SET isRead = 1 WHERE id = :recommendationId")
    suspend fun markAsRead(recommendationId: Long)
    
    @Query("UPDATE ai_recommendations SET isDismissed = 1 WHERE id = :recommendationId")
    suspend fun dismissRecommendation(recommendationId: Long)
    
    @Query("UPDATE ai_recommendations SET isActedUpon = 1 WHERE id = :recommendationId")
    suspend fun markAsActedUpon(recommendationId: Long)
    
    @Query("DELETE FROM ai_recommendations WHERE createdAt < :olderThan AND isDismissed = 1")
    suspend fun cleanupOldRecommendations(olderThan: Long)
    
    @Query("SELECT * FROM user_preferences_ai WHERE id = 1")
    suspend fun getUserAIPreferences(): UserAIPreferences?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAIPreferences(preferences: UserAIPreferences)
    
    @Update
    suspend fun updateUserAIPreferences(preferences: UserAIPreferences)
}
