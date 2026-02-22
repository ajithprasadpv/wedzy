package io.example.wedzy.data.repository

import io.example.wedzy.data.auth.UserSession
import io.example.wedzy.data.local.dao.CollaboratorDao
import io.example.wedzy.data.model.Collaborator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollaboratorRepository @Inject constructor(
    private val collaboratorDao: CollaboratorDao,
    private val userSession: UserSession
) {
    fun getAllCollaborators(): Flow<List<Collaborator>> {
        val userId = userSession.getCurrentUserId()
        return collaboratorDao.getAllCollaborators(userId)
    }
    
    suspend fun getCollaboratorById(collaboratorId: Long): Collaborator? = 
        collaboratorDao.getCollaboratorById(collaboratorId)
    
    suspend fun getCollaboratorByEmail(email: String): Collaborator? {
        val userId = userSession.getCurrentUserId()
        return collaboratorDao.getCollaboratorByEmail(userId, email)
    }
    
    fun getPendingInvites(): Flow<List<Collaborator>> {
        val userId = userSession.getCurrentUserId()
        return collaboratorDao.getPendingInvites(userId)
    }
    
    fun getActiveCollaborators(): Flow<List<Collaborator>> {
        val userId = userSession.getCurrentUserId()
        return collaboratorDao.getActiveCollaborators(userId)
    }
    
    fun getCollaboratorCount(): Flow<Int> {
        val userId = userSession.getCurrentUserId()
        return collaboratorDao.getCollaboratorCount(userId)
    }
    
    suspend fun insertCollaborator(collaborator: Collaborator): Long {
        val userId = userSession.getCurrentUserId()
        return collaboratorDao.insertCollaborator(collaborator.copy(userId = userId))
    }
    
    suspend fun updateCollaborator(collaborator: Collaborator) {
        val userId = userSession.getCurrentUserId()
        collaboratorDao.updateCollaborator(collaborator.copy(userId = userId))
    }
    
    suspend fun deleteCollaborator(collaborator: Collaborator) = 
        collaboratorDao.deleteCollaborator(collaborator)
    
    suspend fun acceptInvite(collaboratorId: Long) = 
        collaboratorDao.acceptInvite(collaboratorId, System.currentTimeMillis())
    
    suspend fun clearUserData() {
        val userId = userSession.getCurrentUserId()
        if (userId.isNotEmpty()) {
            collaboratorDao.deleteAllCollaborators(userId)
        }
    }
}
