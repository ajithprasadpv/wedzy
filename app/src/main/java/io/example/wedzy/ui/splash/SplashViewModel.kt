package io.example.wedzy.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.example.wedzy.data.firebase.FirestoreSyncRepository
import io.example.wedzy.data.local.PreferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashDestination {
    data object Loading : SplashDestination()
    data object Onboarding : SplashDestination()
    data object Main : SplashDestination()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore,
    private val firestoreSync: FirestoreSyncRepository
) : ViewModel() {
    
    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Loading)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()
    
    init {
        checkOnboardingStatus()
    }
    
    private fun checkOnboardingStatus() {
        viewModelScope.launch {
            val isOnboardingCompleted = preferencesDataStore.onboardingCompleted.first()
            if (isOnboardingCompleted) {
                firestoreSync.restoreFromCloud()
                _destination.value = SplashDestination.Main
            } else {
                _destination.value = SplashDestination.Onboarding
            }
        }
    }
}
