package io.example.wedzy.data.local.dao

import androidx.room.*
import io.example.wedzy.data.model.InviteCode
import kotlinx.coroutines.flow.Flow

@Dao
interface InviteCodeDao {
    
    @Query("SELECT * FROM invite_codes WHERE weddingId = :weddingId ORDER BY createdAt DESC")
    fun getInviteCodesByWedding(weddingId: Long): Flow<List<InviteCode>>
    
    @Query("SELECT * FROM invite_codes WHERE code = :code LIMIT 1")
    suspend fun getInviteCodeByCode(code: String): InviteCode?
    
    @Query("SELECT * FROM invite_codes WHERE code = :code AND isUsed = 0 LIMIT 1")
    suspend fun getValidInviteCode(code: String): InviteCode?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInviteCode(code: InviteCode): Long
    
    @Update
    suspend fun updateInviteCode(code: InviteCode)
    
    @Delete
    suspend fun deleteInviteCode(code: InviteCode)
    
    @Query("UPDATE invite_codes SET isUsed = 1, usedByUserId = :userId, usedAt = :usedAt WHERE code = :code")
    suspend fun markCodeAsUsed(code: String, userId: String, usedAt: Long)
    
    @Query("DELETE FROM invite_codes WHERE weddingId = :weddingId")
    suspend fun deleteAllForWedding(weddingId: Long)
}
