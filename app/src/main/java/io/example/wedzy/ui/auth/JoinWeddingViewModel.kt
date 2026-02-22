package io.example.wedzy.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.example.wedzy.data.firebase.FirebaseCollaborationRepository
import io.example.wedzy.data.firebase.WeddingInvite
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JoinWeddingUiState(
    val inviteCode: String = "",
    val weddingInfo: WeddingInvite? = null,
    val isLoading: Boolean = false,
    val joinSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class JoinWeddingViewModel @Inject constructor(
    private val collaborationRepository: FirebaseCollaborationRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(JoinWeddingUiState())
    val uiState: StateFlow<JoinWeddingUiState> = _uiState.asStateFlow()
    
    fun updateInviteCode(code: String) {
        _uiState.update { it.copy(inviteCode = code.take(10), error = null) }
    }
    
    fun validateCode() {
        val code = _uiState.value.inviteCode.trim()
        if (code.length < 6) {
            _uiState.update { it.copy(error = "Invalid code format") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val weddingInvite = collaborationRepository.validateInviteCode(code)
                
                if (weddingInvite != null) {
                    _uiState.update { it.copy(
                        isLoading = false,
                        weddingInfo = weddingInvite
                    )}
                } else {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "Invalid or expired invite code"
                    )}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to validate code"
                )}
            }
        }
    }
    
    fun joinWedding() {
        val weddingInfo = _uiState.value.weddingInfo ?: return
        
        if (!collaborationRepository.isUserLoggedIn) {
            _uiState.update { it.copy(error = "Please sign in first to join a wedding") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val success = collaborationRepository.joinWedding(
                    weddingId = weddingInfo.weddingId,
                    inviteCode = weddingInfo.code
                )
                
                if (success) {
                    _uiState.update { it.copy(isLoading = false, joinSuccess = true) }
                } else {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "Failed to join wedding. Please try again."
                    )}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to join wedding"
                )}
            }
        }
    }
    
    fun clearValidation() {
        _uiState.update { JoinWeddingUiState() }
    }
}
