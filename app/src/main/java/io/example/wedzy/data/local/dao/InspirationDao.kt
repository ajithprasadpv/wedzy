package io.example.wedzy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.example.wedzy.data.model.Inspiration
import kotlinx.coroutines.flow.Flow

@Dao
interface InspirationDao {
    @Query("SELECT * FROM inspirations WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllInspirations(userId: String): Flow<List<Inspiration>>
    
    @Query("SELECT * FROM inspirations WHERE id = :inspirationId")
    suspend fun getInspirationById(inspirationId: Long): Inspiration?
    
    @Query("SELECT * FROM inspirations WHERE userId = :userId AND category = :category ORDER BY createdAt DESC")
    fun getInspirationsByCategory(userId: String, category: String): Flow<List<Inspiration>>
    
    @Query("SELECT * FROM inspirations WHERE userId = :userId AND isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteInspirations(userId: String): Flow<List<Inspiration>>
    
    @Query("SELECT COUNT(*) FROM inspirations WHERE userId = :userId")
    fun getInspirationCount(userId: String): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspiration(inspiration: Inspiration): Long
    
    @Update
    suspend fun updateInspiration(inspiration: Inspiration)
    
    @Delete
    suspend fun deleteInspiration(inspiration: Inspiration)
    
    @Query("UPDATE inspirations SET isFavorite = :isFavorite WHERE id = :inspirationId")
    suspend fun updateFavoriteStatus(inspirationId: Long, isFavorite: Boolean)
}
