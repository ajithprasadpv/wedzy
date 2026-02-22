package io.example.wedzy.ui.collaboration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.example.wedzy.data.model.Collaborator
import io.example.wedzy.data.model.CollaboratorPermission
import io.example.wedzy.data.model.CollaboratorRole
import io.example.wedzy.data.model.InviteCode
import io.example.wedzy.data.repository.CollaboratorRepository
import io.example.wedzy.data.repository.InviteCodeRepository
import io.example.wedzy.data.repository.WeddingProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CollaborationUiState(
    val collaborators: List<Collaborator> = emptyList(),
    val pendingInvites: List<Collaborator> = emptyList(),
    val activeCollaborators: List<Collaborator> = emptyList(),
    val inviteCodes: List<InviteCode> = emptyList(),
    val currentInviteCode: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class CollaborationViewModel @Inject constructor(
    private val collaboratorRepository: CollaboratorRepository,
    private val inviteCodeRepository: InviteCodeRepository,
    private val weddingProfileRepository: WeddingProfileRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CollaborationUiState())
    val uiState: StateFlow<CollaborationUiState> = _uiState.asStateFlow()
    
    init {
        loadCollaborators()
    }
    
    private fun loadCollaborators() {
        viewModelScope.launch {
            collaboratorRepository.getAllCollaborators().collect { collaborators ->
                _uiState.update { state ->
                    state.copy(
                        collaborators = collaborators,
                        pendingInvites = collaborators.filter { it.isInvitePending },
                        activeCollaborators = collaborators.filter { !it.isInvitePending },
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun inviteCollaborator(
        name: String,
        email: String,
        role: CollaboratorRole,
        canEditTasks: Boolean = false,
        canEditBudget: Boolean = false,
        canEditGuests: Boolean = false,
        canEditVendors: Boolean = false
    ) {
        viewModelScope.launch {
            val collaborator = Collaborator(
                name = name.trim(),
                email = email.trim(),
                role = role,
                permission = if (role == CollaboratorRole.PARTNER) CollaboratorPermission.FULL_ACCESS
                            else CollaboratorPermission.SPECIFIC_SECTIONS,
                isInvitePending = true,
                canEditTasks = canEditTasks || role == CollaboratorRole.PARTNER,
                canEditBudget = canEditBudget || role == CollaboratorRole.PARTNER,
                canEditGuests = canEditGuests || role == CollaboratorRole.PARTNER,
                canEditVendors = canEditVendors || role == CollaboratorRole.PARTNER
            )
            collaboratorRepository.insertCollaborator(collaborator)
        }
    }
    
    fun removeCollaborator(collaborator: Collaborator) {
        viewModelScope.launch {
            collaboratorRepository.deleteCollaborator(collaborator)
        }
    }
    
    fun updatePermissions(
        collaborator: Collaborator,
        canEditTasks: Boolean,
        canEditBudget: Boolean,
        canEditGuests: Boolean,
        canEditVendors: Boolean
    ) {
        viewModelScope.launch {
            val updated = collaborator.copy(
                canEditTasks = canEditTasks,
                canEditBudget = canEditBudget,
                canEditGuests = canEditGuests,
                canEditVendors = canEditVendors
            )
            collaboratorRepository.updateCollaborator(updated)
        }
    }
    
    fun resendInvite(collaborator: Collaborator) {
        // In a real app, this would trigger an email/notification
    }
    
    suspend fun generateInviteCodeForContact(
        name: String,
        phone: String,
        role: CollaboratorRole = CollaboratorRole.FRIEND
    ): String {
        val profile = weddingProfileRepository.getProfileOnce()
        val weddingId = profile?.id ?: 0L
        
        val inviteCode = inviteCodeRepository.createInviteCode(
            weddingId = weddingId,
            invitedName = name,
            invitedPhone = phone,
            role = role
        )
        
        val collaborator = Collaborator(
            name = name,
            phone = phone,
            inviteCode = inviteCode.code,
            role = role,
            isInvitePending = true
        )
        collaboratorRepository.insertCollaborator(collaborator)
        
        return inviteCode.code
    }
    
    fun addCollaboratorsFromContacts(contacts: List<Pair<String, String?>>) {
        viewModelScope.launch {
            val profile = weddingProfileRepository.getProfileOnce()
            val weddingId = profile?.id ?: 0L
            
            contacts.forEach { (name, phone) ->
                phone?.let {
                    val inviteCode = inviteCodeRepository.createInviteCode(
                        weddingId = weddingId,
                        invitedName = name,
                        invitedPhone = it,
                        role = CollaboratorRole.FRIEND
                    )
                    
                    val collaborator = Collaborator(
                        name = name,
                        phone = it,
                        inviteCode = inviteCode.code,
                        role = CollaboratorRole.FRIEND,
                        isInvitePending = true
                    )
                    collaboratorRepository.insertCollaborator(collaborator)
                }
            }
        }
    }
    
    suspend fun getCurrentWeddingInviteCode(): String {
        val profile = weddingProfileRepository.getProfileOnce()
        val weddingId = profile?.id ?: 0L
        
        val inviteCode = inviteCodeRepository.createInviteCode(
            weddingId = weddingId,
            invitedName = "General",
            invitedPhone = "",
            role = CollaboratorRole.FRIEND
        )
        
        return inviteCode.code
    }
}
