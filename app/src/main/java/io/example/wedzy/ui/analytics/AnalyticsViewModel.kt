package io.example.wedzy.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.example.wedzy.data.local.PreferencesDataStore
import io.example.wedzy.data.model.BudgetCategory
import io.example.wedzy.data.model.BudgetItem
import io.example.wedzy.data.model.Currency
import io.example.wedzy.data.repository.BudgetRepository
import io.example.wedzy.data.repository.GuestRepository
import io.example.wedzy.data.repository.TaskRepository
import io.example.wedzy.data.repository.VendorRepository
import io.example.wedzy.data.repository.WeddingProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategorySpending(
    val category: BudgetCategory,
    val estimated: Double,
    val actual: Double,
    val percentage: Float
)

private data class BudgetData(
    val items: List<BudgetItem>,
    val estimated: Double,
    val actual: Double,
    val paid: Double,
    val totalBudget: Double
)

private data class AnalyticsBudgetState(
    val totalBudget: Double,
    val totalEstimated: Double,
    val totalActual: Double,
    val totalPaid: Double,
    val remainingBudget: Double,
    val budgetUtilization: Float,
    val categorySpending: List<CategorySpending>,
    val topExpenses: List<BudgetItem>,
    val currency: Currency
)

data class AnalyticsUiState(
    val totalBudget: Double = 0.0,
    val totalEstimated: Double = 0.0,
    val totalActual: Double = 0.0,
    val totalPaid: Double = 0.0,
    val remainingBudget: Double = 0.0,
    val budgetUtilization: Float = 0f,
    val categorySpending: List<CategorySpending> = emptyList(),
    val topExpenses: List<BudgetItem> = emptyList(),
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val taskCompletionRate: Float = 0f,
    val totalGuests: Int = 0,
    val confirmedGuests: Int = 0,
    val guestConfirmationRate: Float = 0f,
    val totalVendors: Int = 0,
    val bookedVendors: Int = 0,
    val currency: Currency = Currency.USD,
    val isLoading: Boolean = true
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val taskRepository: TaskRepository,
    private val guestRepository: GuestRepository,
    private val vendorRepository: VendorRepository,
    private val weddingProfileRepository: WeddingProfileRepository,
    private val preferencesDataStore: PreferencesDataStore
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()
    
    init {
        loadAnalytics()
    }
    
    private fun loadAnalytics() {
        viewModelScope.launch {
            combine(
                budgetRepository.getAllBudgetItems(),
                budgetRepository.getTotalEstimatedCost(),
                budgetRepository.getTotalActualCost(),
                budgetRepository.getTotalPaidAmount(),
                weddingProfileRepository.getProfile()
            ) { items, estimated, actual, paid, profile ->
                BudgetData(items, estimated, actual, paid, profile?.totalBudget ?: 0.0)
            }.combine(preferencesDataStore.selectedCurrency) { budgetData, currencyCode ->
                val budgetToUse = if (budgetData.totalBudget > 0) budgetData.totalBudget else budgetData.estimated
                
                val categorySpending = BudgetCategory.entries.mapNotNull { category ->
                    val categoryItems = budgetData.items.filter { it.category == category }
                    if (categoryItems.isEmpty()) null
                    else {
                        val catEstimated = categoryItems.sumOf { it.estimatedCost }
                        val catActual = categoryItems.sumOf { it.actualCost }
                        CategorySpending(
                            category = category,
                            estimated = catEstimated,
                            actual = catActual,
                            percentage = if (budgetToUse > 0) (catActual / budgetToUse * 100).toFloat() else 0f
                        )
                    }
                }.sortedByDescending { it.actual }
                
                val topExpenses = budgetData.items.sortedByDescending { 
                    it.actualCost.takeIf { c -> c > 0 } ?: it.estimatedCost 
                }.take(5)
                
                AnalyticsBudgetState(
                    totalBudget = budgetData.totalBudget,
                    totalEstimated = budgetData.estimated,
                    totalActual = budgetData.actual,
                    totalPaid = budgetData.paid,
                    remainingBudget = budgetToUse - budgetData.actual,
                    budgetUtilization = if (budgetToUse > 0) (budgetData.actual / budgetToUse).toFloat() else 0f,
                    categorySpending = categorySpending,
                    topExpenses = topExpenses,
                    currency = Currency.fromCode(currencyCode)
                )
            }.collect { budgetState ->
                _uiState.update { it.copy(
                    totalBudget = budgetState.totalBudget,
                    totalEstimated = budgetState.totalEstimated,
                    totalActual = budgetState.totalActual,
                    totalPaid = budgetState.totalPaid,
                    remainingBudget = budgetState.remainingBudget,
                    budgetUtilization = budgetState.budgetUtilization,
                    categorySpending = budgetState.categorySpending,
                    topExpenses = budgetState.topExpenses,
                    currency = budgetState.currency,
                    isLoading = false
                )}
            }
        }
        
        viewModelScope.launch {
            taskRepository.getAllTasks().collect { tasks ->
                val completed = tasks.count { it.status.name == "COMPLETED" }
                _uiState.update { it.copy(
                    totalTasks = tasks.size,
                    completedTasks = completed,
                    taskCompletionRate = if (tasks.isNotEmpty()) completed.toFloat() / tasks.size else 0f
                )}
            }
        }
        
        viewModelScope.launch {
            guestRepository.getAllGuests().collect { guests ->
                val confirmed = guests.count { it.rsvpStatus.name == "CONFIRMED" }
                _uiState.update { it.copy(
                    totalGuests = guests.size,
                    confirmedGuests = confirmed,
                    guestConfirmationRate = if (guests.isNotEmpty()) confirmed.toFloat() / guests.size else 0f
                )}
            }
        }
        
        viewModelScope.launch {
            vendorRepository.getAllVendors().collect { vendors ->
                val booked = vendors.count { it.status.name in listOf("BOOKED", "DEPOSIT_PAID", "COMPLETED") }
                _uiState.update { it.copy(
                    totalVendors = vendors.size,
                    bookedVendors = booked
                )}
            }
        }
    }
}
