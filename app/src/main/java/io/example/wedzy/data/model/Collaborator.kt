package io.example.wedzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CollaboratorRole {
    OWNER, PARTNER, PLANNER, FAMILY, FRIEND, VIEW_ONLY
}

enum class CollaboratorPermission {
    FULL_ACCESS, EDIT, VIEW_ONLY, SPECIFIC_SECTIONS
}

@Entity(tableName = "collaborators")
data class Collaborator(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "",
    val name: String,
    val email: String = "",
    val phone: String = "",
    val inviteCode: String = "",
    val role: CollaboratorRole = CollaboratorRole.VIEW_ONLY,
    val permission: CollaboratorPermission = CollaboratorPermission.VIEW_ONLY,
    val avatarUrl: String = "",
    val isInvitePending: Boolean = true,
    val invitedAt: Long = System.currentTimeMillis(),
    val joinedAt: Long? = null,
    val canEditTasks: Boolean = false,
    val canEditBudget: Boolean = false,
    val canEditGuests: Boolean = false,
    val canEditVendors: Boolean = false,
    val canEditSeating: Boolean = false,
    val lastActiveAt: Long? = null
)
