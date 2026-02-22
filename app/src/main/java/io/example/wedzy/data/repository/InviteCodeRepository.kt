package io.example.wedzy.data.repository

import io.example.wedzy.data.local.WedzyDatabase
import io.example.wedzy.data.model.InviteCode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InviteCodeRepository @Inject constructor(
    private val database: WedzyDatabase
) {
    private val inviteCodeDao = database.inviteCodeDao()
    
    fun getInviteCodesByWedding(weddingId: Long): Flow<List<InviteCode>> {
        return inviteCodeDao.getInviteCodesByWedding(weddingId)
    }
    
    suspend fun getInviteCodeByCode(code: String): InviteCode? {
        return inviteCodeDao.getInviteCodeByCode(code)
    }
    
    suspend fun validateInviteCode(code: String): InviteCode? {
        return inviteCodeDao.getValidInviteCode(code)
    }
    
    suspend fun createInviteCode(
        weddingId: Long,
        invitedName: String,
        invitedPhone: String,
        role: io.example.wedzy.data.model.CollaboratorRole
    ): InviteCode {
        val code = InviteCode(
            weddingId = weddingId,
            invitedName = invitedName,
            invitedPhone = invitedPhone,
            role = role
        )
        inviteCodeDao.insertInviteCode(code)
        return code
    }
    
    suspend fun markCodeAsUsed(code: String, userId: String) {
        inviteCodeDao.markCodeAsUsed(code, userId, System.currentTimeMillis())
    }
    
    suspend fun deleteInviteCode(code: InviteCode) {
        inviteCodeDao.deleteInviteCode(code)
    }
}
