package io.example.wedzy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.example.wedzy.data.local.PreferencesDataStore
import io.example.wedzy.data.model.Currency
import io.example.wedzy.data.model.Task
import io.example.wedzy.data.model.TaskStatus
import io.example.wedzy.data.model.WeddingProfile
import io.example.wedzy.data.repository.BudgetRepository
import io.example.wedzy.data.repository.GuestRepository
import io.example.wedzy.data.repository.TaskRepository
import io.example.wedzy.data.repository.VendorRepository
import io.example.wedzy.data.repository.WeddingEventRepository
import io.example.wedzy.data.repository.WeddingProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val profile: WeddingProfile? = null,
    val daysUntilWedding: Int = 0,
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val totalBudget: Double = 0.0,
    val spentBudget: Double = 0.0,
    val totalGuests: Int = 0,
    val confirmedGuests: Int = 0,
    val totalVendors: Int = 0,
    val bookedVendors: Int = 0,
    val upcomingEvents: Int = 0,
    val upcomingTasks: List<Task> = emptyList(),
    val selectedCurrency: Currency = Currency.USD,
    val heroBackgroundImage: String? = null,
    val isLoading: Boolean = true,
    val currentUser: FirebaseUser? = null,
    val isSignedIn: Boolean = false
)

private data class DashboardData(
    val profile: WeddingProfile?,
    val daysUntilWedding: Int,
    val totalTasks: Int,
    val completedTasks: Int,
    val totalBudget: Double,
    val spentBudget: Double,
    val totalGuests: Int,
    val confirmedGuests: Int,
    val totalVendors: Int,
    val upcomingTasks: List<Task>,
    val upcomingEvents: Int
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weddingProfileRepository: WeddingProfileRepository,
    private val taskRepository: TaskRepository,
    private val budgetRepository: BudgetRepository,
    private val guestRepository: GuestRepository,
    private val vendorRepository: VendorRepository,
    private val weddingEventRepository: WeddingEventRepository,
    private val preferencesDataStore: PreferencesDataStore
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        _uiState.update { it.copy(currentUser = user, isSignedIn = user != null) }
    }
    
    init {
        loadDashboardData()
        loadPreferences()
        loadAuthState()
    }
    
    private fun loadAuthState() {
        val auth = FirebaseAuth.getInstance()
        auth.addAuthStateListener(authStateListener)
        val currentUser = auth.currentUser
        _uiState.update { it.copy(currentUser = currentUser, isSignedIn = currentUser != null) }
    }
    
    override fun onCleared() {
        super.onCleared()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }
    
    private fun loadPreferences() {
        viewModelScope.launch {
            preferencesDataStore.selectedCurrency.collect { currencyCode ->
                _uiState.update { it.copy(selectedCurrency = Currency.fromCode(currencyCode)) }
            }
        }
        viewModelScope.launch {
            preferencesDataStore.heroBackgroundImage.collect { imageUri ->
                _uiState.update { it.copy(heroBackgroundImage = imageUri) }
            }
        }
    }
    
    private fun loadDashboardData() {
        viewModelScope.launch {
            combine(
                weddingProfileRepository.getProfile(),
                taskRepository.getTaskCount(),
                taskRepository.getCompletedTaskCount(),
                taskRepository.getActiveTasks(),
                budgetRepository.getTotalEstimatedCost(),
                budgetRepository.getTotalActualCost(),
                guestRepository.getTotalGuestCount(),
                guestRepository.getConfirmedAttendeeCount(),
                vendorRepository.getVendorCount(),
                weddingEventRepository.getEventCount()
            ) { values ->
                val profile = values[0] as? WeddingProfile
                val totalTasks = values[1] as Int
                val completedTasks = values[2] as Int
                val activeTasks = (values[3] as List<*>).filterIsInstance<Task>()
                val estimatedBudget = values[4] as Double
                val actualBudget = values[5] as Double
                val totalGuests = values[6] as Int
                val confirmedGuests = values[7] as Int
                val totalVendors = values[8] as Int
                val eventCount = values[9] as Int
                
                val daysUntil = if (profile != null && profile.weddingDate > 0) {
                    ((profile.weddingDate - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
                } else 0
                
                DashboardData(
                    profile = profile,
                    daysUntilWedding = daysUntil,
                    totalTasks = totalTasks,
                    completedTasks = completedTasks,
                    totalBudget = profile?.totalBudget ?: estimatedBudget,
                    spentBudget = actualBudget,
                    totalGuests = totalGuests,
                    confirmedGuests = confirmedGuests,
                    totalVendors = totalVendors,
                    upcomingTasks = activeTasks.take(5),
                    upcomingEvents = eventCount
                )
            }.collect { data ->
                _uiState.update { currentState ->
                    currentState.copy(
                        profile = data.profile,
                        daysUntilWedding = data.daysUntilWedding,
                        totalTasks = data.totalTasks,
                        completedTasks = data.completedTasks,
                        totalBudget = data.totalBudget,
                        spentBudget = data.spentBudget,
                        totalGuests = data.totalGuests,
                        confirmedGuests = data.confirmedGuests,
                        totalVendors = data.totalVendors,
                        bookedVendors = 0,
                        upcomingEvents = data.upcomingEvents,
                        upcomingTasks = data.upcomingTasks,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun completeTask(task: Task) {
        viewModelScope.launch {
            taskRepository.completeTask(task)
        }
    }
    
    fun updateWeddingDetails(brideName: String, groomName: String, weddingDate: Long, totalBudget: Double, currency: Currency) {
        viewModelScope.launch {
            val existingProfile = _uiState.value.profile
            val updatedProfile = if (existingProfile != null) {
                existingProfile.copy(
                    brideName = brideName,
                    groomName = groomName,
                    weddingDate = weddingDate,
                    totalBudget = totalBudget
                )
            } else {
                WeddingProfile(
                    brideName = brideName,
                    groomName = groomName,
                    weddingDate = weddingDate,
                    totalBudget = totalBudget
                )
            }
            weddingProfileRepository.saveProfile(updatedProfile)
            preferencesDataStore.setSelectedCurrency(currency.code)
            _uiState.update { it.copy(selectedCurrency = currency) }
        }
    }
    
    fun updateHeroBackgroundImage(imageUri: String?) {
        viewModelScope.launch {
            preferencesDataStore.setHeroBackgroundImage(imageUri)
            _uiState.update { it.copy(heroBackgroundImage = imageUri) }
        }
    }
    
    fun logout() {
        FirebaseAuth.getInstance().signOut()
        _uiState.update { it.copy(currentUser = null, isSignedIn = false) }
    }
}
