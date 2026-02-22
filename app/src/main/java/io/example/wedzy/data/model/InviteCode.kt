package io.example.wedzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "invite_codes")
data class InviteCode(
    @PrimaryKey
    val code: String = generateInviteCode(),
    val weddingId: Long = 0,
    val invitedName: String = "",
    val invitedPhone: String = "",
    val role: CollaboratorRole = CollaboratorRole.FRIEND,
    val isUsed: Boolean = false,
    val usedByUserId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val usedAt: Long? = null,
    val expiresAt: Long? = null
) {
    companion object {
        fun generateInviteCode(): String {
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            return (1..10)
                .map { chars.random() }
                .joinToString("")
        }
    }
}
