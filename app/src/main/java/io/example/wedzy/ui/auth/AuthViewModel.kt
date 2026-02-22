package io.example.wedzy.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.example.wedzy.data.firebase.FirebaseAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val isLoading: Boolean = false,
    val isGoogleLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    init {
        _uiState.update { it.copy(isAuthenticated = authRepository.isLoggedIn) }
    }
    
    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }
    
    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }
    
    fun updateDisplayName(name: String) {
        _uiState.update { it.copy(displayName = name, error = null) }
    }
    
    fun signUp() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank() || state.displayName.isBlank()) {
            _uiState.update { it.copy(error = "Please fill in all fields") }
            return
        }
        
        if (state.password.length < 6) {
            _uiState.update { it.copy(error = "Password must be at least 6 characters") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = authRepository.signUpWithEmail(
                email = state.email.trim(),
                password = state.password,
                displayName = state.displayName.trim()
            )
            
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Sign up failed") }
                }
            )
        }
    }
    
    fun signIn() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Please enter email and password") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = authRepository.signInWithEmail(
                email = state.email.trim(),
                password = state.password
            )
            
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Sign in failed") }
                }
            )
        }
    }
    
    fun signOut() {
        authRepository.signOut()
        _uiState.update { AuthUiState() }
    }
    
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGoogleLoading = true, error = null) }
            
            val result = authRepository.signInWithGoogle(idToken)
            
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isGoogleLoading = false, isAuthenticated = true) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isGoogleLoading = false, error = e.message ?: "Google sign in failed") }
                }
            )
        }
    }
    
    fun setGoogleLoading(loading: Boolean) {
        _uiState.update { it.copy(isGoogleLoading = loading) }
    }
    
    fun setError(error: String?) {
        _uiState.update { it.copy(error = error) }
    }
}
