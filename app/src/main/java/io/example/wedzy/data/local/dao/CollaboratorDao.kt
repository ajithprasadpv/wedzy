package io.example.wedzy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.example.wedzy.data.model.Collaborator
import kotlinx.coroutines.flow.Flow

@Dao
interface CollaboratorDao {
    @Query("SELECT * FROM collaborators WHERE userId = :userId ORDER BY name ASC")
    fun getAllCollaborators(userId: String): Flow<List<Collaborator>>
    
    @Query("SELECT * FROM collaborators WHERE id = :collaboratorId")
    suspend fun getCollaboratorById(collaboratorId: Long): Collaborator?
    
    @Query("SELECT * FROM collaborators WHERE userId = :userId AND email = :email")
    suspend fun getCollaboratorByEmail(userId: String, email: String): Collaborator?
    
    @Query("SELECT * FROM collaborators WHERE userId = :userId AND isInvitePending = 1")
    fun getPendingInvites(userId: String): Flow<List<Collaborator>>
    
    @Query("SELECT * FROM collaborators WHERE userId = :userId AND isInvitePending = 0")
    fun getActiveCollaborators(userId: String): Flow<List<Collaborator>>
    
    @Query("SELECT COUNT(*) FROM collaborators WHERE userId = :userId")
    fun getCollaboratorCount(userId: String): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollaborator(collaborator: Collaborator): Long
    
    @Update
    suspend fun updateCollaborator(collaborator: Collaborator)
    
    @Delete
    suspend fun deleteCollaborator(collaborator: Collaborator)
    
    @Query("UPDATE collaborators SET isInvitePending = 0, joinedAt = :joinedAt WHERE id = :collaboratorId")
    suspend fun acceptInvite(collaboratorId: Long, joinedAt: Long)
    
    @Query("DELETE FROM collaborators WHERE userId = :userId")
    suspend fun deleteAllCollaborators(userId: String)
}
