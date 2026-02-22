package io.example.wedzy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.example.wedzy.data.model.WeddingProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface WeddingProfileDao {
    
    @Query("SELECT * FROM wedding_profile WHERE userId = :userId LIMIT 1")
    fun getProfile(userId: String): Flow<WeddingProfile?>
    
    @Query("SELECT * FROM wedding_profile WHERE userId = :userId LIMIT 1")
    suspend fun getProfileOnce(userId: String): WeddingProfile?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: WeddingProfile): Long
    
    @Update
    suspend fun updateProfile(profile: WeddingProfile)
    
    @Delete
    suspend fun deleteProfile(profile: WeddingProfile)
    
    @Query("DELETE FROM wedding_profile WHERE userId = :userId")
    suspend fun deleteAll(userId: String)
}
