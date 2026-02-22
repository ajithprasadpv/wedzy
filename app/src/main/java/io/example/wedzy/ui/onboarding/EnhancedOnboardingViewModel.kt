package io.example.wedzy.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.example.wedzy.data.local.PreferencesDataStore
import io.example.wedzy.data.model.*
import io.example.wedzy.data.repository.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnhancedOnboardingViewModel @Inject constructor(
    private val weddingProfileRepository: WeddingProfileRepository,
    private val taskRepository: TaskRepository,
    private val budgetRepository: BudgetRepository,
    private val guestRepository: GuestRepository,
    private val vendorRepository: VendorRepository,
    private val preferencesDataStore: PreferencesDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingState())
    val uiState: StateFlow<OnboardingState> = _uiState.asStateFlow()

    fun updateBrideName(name: String) {
        _uiState.update { it.copy(brideName = name) }
    }

    fun updateGroomName(name: String) {
        _uiState.update { it.copy(groomName = name) }
    }

    fun updateWeddingDate(date: Long?) {
        _uiState.update { it.copy(weddingDate = date) }
    }

    fun updateBudget(budget: String) {
        _uiState.update { it.copy(totalBudget = budget) }
    }

    fun updateCurrency(currency: Currency) {
        _uiState.update { it.copy(selectedCurrency = currency) }
    }

    fun updateNotificationPreference(enabled: Boolean) {
        _uiState.update { it.copy(wantsNotifications = enabled) }
    }

    fun updateSampleDataPreference(enabled: Boolean) {
        _uiState.update { it.copy(wantsSampleData = enabled) }
    }

    fun nextStep() {
        val currentIndex = _uiState.value.currentStepIndex
        val steps = OnboardingStep.values()
        if (currentIndex < steps.size - 1) {
            _uiState.update { it.copy(currentStep = steps[currentIndex + 1]) }
        }
    }

    fun previousStep() {
        val currentIndex = _uiState.value.currentStepIndex
        val steps = OnboardingStep.values()
        if (currentIndex > 0) {
            _uiState.update { it.copy(currentStep = steps[currentIndex - 1]) }
        }
    }

    fun skipOnboarding() {
        viewModelScope.launch {
            preferencesDataStore.setOnboardingCompleted(true)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCompleting = true) }
            
            try {
                val state = _uiState.value
                
                // Create wedding profile
                val profile = WeddingProfile(
                    brideName = state.brideName,
                    groomName = state.groomName,
                    weddingDate = state.weddingDate ?: 0L,
                    totalBudget = state.totalBudget.toDoubleOrNull() ?: 0.0,
                    currency = state.selectedCurrency.code
                )
                weddingProfileRepository.saveProfile(profile)
                
                // Set currency preference
                preferencesDataStore.setSelectedCurrency(state.selectedCurrency.code)
                
                // Generate sample data if requested
                if (state.wantsSampleData) {
                    generateSampleData(state)
                }
                
                // Generate task templates
                if (state.weddingDate != null) {
                    taskRepository.generateTasksFromTemplate(state.weddingDate)
                }
                
                // Mark onboarding as completed
                preferencesDataStore.setOnboardingCompleted(true)
                
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isCompleting = false) }
            }
        }
    }

    private suspend fun generateSampleData(state: OnboardingState) {
        // Sample budget items
        val sampleBudgetItems = listOf(
            BudgetItem(
                name = "Wedding Venue",
                category = BudgetCategory.VENUE,
                estimatedCost = (state.totalBudget.toDouble() * 0.45),
                actualCost = 0.0,
                notes = "Sample venue booking"
            ),
            BudgetItem(
                name = "Catering Service",
                category = BudgetCategory.CATERING,
                estimatedCost = (state.totalBudget.toDouble() * 0.28),
                actualCost = 0.0,
                notes = "Sample catering for 100 guests"
            ),
            BudgetItem(
                name = "Photography & Videography",
                category = BudgetCategory.PHOTOGRAPHY,
                estimatedCost = (state.totalBudget.toDouble() * 0.12),
                actualCost = 0.0,
                notes = "Sample photographer package"
            ),
            BudgetItem(
                name = "Flowers & Decorations",
                category = BudgetCategory.FLOWERS,
                estimatedCost = (state.totalBudget.toDouble() * 0.08),
                actualCost = 0.0,
                notes = "Sample floral arrangements"
            ),
            BudgetItem(
                name = "Wedding Attire",
                category = BudgetCategory.ATTIRE_BRIDE,
                estimatedCost = (state.totalBudget.toDouble() * 0.07),
                actualCost = 0.0,
                notes = "Sample dress and suit"
            )
        )
        budgetRepository.insertBudgetItems(sampleBudgetItems)

        // Sample guests
        val sampleGuests = listOf(
            Guest(
                firstName = "John",
                lastName = "Smith",
                email = "john.smith@example.com",
                phone = "555-0101",
                rsvpStatus = RsvpStatus.CONFIRMED,
                side = GuestSide.BRIDE,
                plusOneAllowed = true,
                plusOneConfirmed = true
            ),
            Guest(
                firstName = "Sarah",
                lastName = "Johnson",
                email = "sarah.j@example.com",
                phone = "555-0102",
                rsvpStatus = RsvpStatus.CONFIRMED,
                side = GuestSide.GROOM,
                plusOneAllowed = false
            ),
            Guest(
                firstName = "Michael",
                lastName = "Brown",
                email = "m.brown@example.com",
                rsvpStatus = RsvpStatus.PENDING,
                side = GuestSide.MUTUAL,
                plusOneAllowed = true
            )
        )
        guestRepository.insertGuests(sampleGuests)

        // Sample vendors
        val sampleVendors = listOf(
            Vendor(
                name = "Elegant Venues",
                category = VendorCategory.VENUE,
                contactPerson = "Jane Doe",
                phone = "555-1001",
                email = "contact@elegantvenues.com",
                status = VendorStatus.BOOKED,
                quotedPrice = state.totalBudget.toDouble() * 0.45,
                agreedPrice = state.totalBudget.toDouble() * 0.45
            ),
            Vendor(
                name = "Gourmet Catering Co.",
                category = VendorCategory.CATERER,
                contactPerson = "Chef Robert",
                phone = "555-1002",
                email = "info@gourmetcatering.com",
                status = VendorStatus.CONTACTED,
                quotedPrice = state.totalBudget.toDouble() * 0.28
            ),
            Vendor(
                name = "Perfect Moments Photography",
                category = VendorCategory.PHOTOGRAPHER,
                contactPerson = "Lisa Chen",
                phone = "555-1003",
                email = "lisa@perfectmoments.com",
                status = VendorStatus.PROPOSAL_RECEIVED,
                quotedPrice = state.totalBudget.toDouble() * 0.12
            )
        )
        vendorRepository.insertVendors(sampleVendors)
    }
}
