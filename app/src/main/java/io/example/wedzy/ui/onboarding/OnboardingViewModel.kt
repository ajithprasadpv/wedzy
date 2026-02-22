package io.example.wedzy.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.example.wedzy.data.local.PreferencesDataStore
import io.example.wedzy.data.model.Currency
import io.example.wedzy.data.model.WeddingProfile
import io.example.wedzy.data.repository.WeddingProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val brideName: String = "",
    val groomName: String = "",
    val weddingDate: Long = 0L,
    val estimatedBudget: String = "",
    val selectedCurrency: Currency = Currency.USD,
    val currentStep: Int = 0,
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val weddingProfileRepository: WeddingProfileRepository,
    private val preferencesDataStore: PreferencesDataStore
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()
    
    fun updateBrideName(name: String) {
        _uiState.update { it.copy(brideName = name) }
    }
    
    fun updateGroomName(name: String) {
        _uiState.update { it.copy(groomName = name) }
    }
    
    fun updateWeddingDate(date: Long) {
        _uiState.update { it.copy(weddingDate = date) }
    }
    
    fun updateEstimatedBudget(budget: String) {
        _uiState.update { it.copy(estimatedBudget = budget) }
    }
    
    fun updateCurrency(currency: Currency) {
        _uiState.update { it.copy(selectedCurrency = currency) }
    }
    
    fun nextStep() {
        _uiState.update { it.copy(currentStep = it.currentStep + 1) }
    }
    
    fun previousStep() {
        _uiState.update { it.copy(currentStep = maxOf(0, it.currentStep - 1)) }
    }
    
    fun canProceed(): Boolean {
        return when (_uiState.value.currentStep) {
            0 -> _uiState.value.brideName.isNotBlank() && _uiState.value.groomName.isNotBlank()
            1 -> _uiState.value.weddingDate > 0
            2 -> true
            else -> true
        }
    }
    
    fun completeOnboarding() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val budget = _uiState.value.estimatedBudget.toDoubleOrNull() ?: 0.0
                val profile = WeddingProfile(
                    brideName = _uiState.value.brideName.trim(),
                    groomName = _uiState.value.groomName.trim(),
                    weddingDate = _uiState.value.weddingDate,
                    totalBudget = budget
                )
                val profileId = weddingProfileRepository.saveProfile(profile)
                preferencesDataStore.setWeddingProfileId(profileId)
                preferencesDataStore.setOnboardingCompleted(true)
                preferencesDataStore.setSelectedCurrency(_uiState.value.selectedCurrency.code)
                _uiState.update { it.copy(isLoading = false, isCompleted = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
