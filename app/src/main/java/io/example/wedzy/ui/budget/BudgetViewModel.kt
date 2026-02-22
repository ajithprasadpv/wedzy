package io.example.wedzy.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.example.wedzy.data.local.PreferencesDataStore
import io.example.wedzy.data.model.BudgetCategory
import io.example.wedzy.data.model.BudgetItem
import io.example.wedzy.data.model.Currency
import io.example.wedzy.data.model.PaymentStatus
import io.example.wedzy.data.repository.BudgetRepository
import io.example.wedzy.data.repository.WeddingProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BudgetUiState(
    val items: List<BudgetItem> = emptyList(),
    val totalBudget: Double = 0.0,
    val totalEstimated: Double = 0.0,
    val totalActual: Double = 0.0,
    val totalPaid: Double = 0.0,
    val selectedCategory: BudgetCategory? = null,
    val selectedCurrency: Currency = Currency.USD,
    val isLoading: Boolean = true,
    val budgetAlert: BudgetAlert? = null,
    val categoryAlerts: Map<BudgetCategory, String> = emptyMap()
)

data class BudgetAlert(
    val message: String,
    val severity: AlertSeverity
)

enum class AlertSeverity {
    INFO, WARNING, CRITICAL
}

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val weddingProfileRepository: WeddingProfileRepository,
    private val preferencesDataStore: PreferencesDataStore
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()
    
    init {
        loadBudgetData()
    }
    
    private fun loadBudgetData() {
        viewModelScope.launch {
            combine(
                budgetRepository.getAllBudgetItems(),
                budgetRepository.getTotalEstimatedCost(),
                budgetRepository.getTotalActualCost(),
                budgetRepository.getTotalPaidAmount(),
                weddingProfileRepository.getProfile()
            ) { items, estimated, actual, paid, profile ->
                BudgetDataHolder(items, estimated, actual, paid, profile?.totalBudget ?: 0.0)
            }.combine(preferencesDataStore.selectedCurrency) { data, currencyCode ->
                val alert = calculateBudgetAlert(data.totalBudget, data.actual)
                val categoryAlerts = calculateCategoryAlerts(data.items)
                
                BudgetUiState(
                    items = data.items,
                    totalBudget = data.totalBudget,
                    totalEstimated = data.estimated,
                    totalActual = data.actual,
                    totalPaid = data.paid,
                    selectedCurrency = Currency.fromCode(currencyCode),
                    isLoading = false,
                    budgetAlert = alert,
                    categoryAlerts = categoryAlerts
                )
            }.collect { state ->
                _uiState.update { state }
            }
        }
    }
    
    private data class BudgetDataHolder(
        val items: List<BudgetItem>,
        val estimated: Double,
        val actual: Double,
        val paid: Double,
        val totalBudget: Double
    )
    
    fun setCurrency(currency: Currency) {
        viewModelScope.launch {
            preferencesDataStore.setSelectedCurrency(currency.code)
        }
    }
    
    fun deleteItem(item: BudgetItem) {
        viewModelScope.launch {
            budgetRepository.deleteBudgetItem(item)
        }
    }
    
    private fun calculateBudgetAlert(totalBudget: Double, actualSpent: Double): BudgetAlert? {
        if (totalBudget <= 0) return null
        
        val percentageSpent = (actualSpent / totalBudget) * 100
        
        return when {
            percentageSpent >= 100 -> BudgetAlert(
                "Budget exceeded! You've spent ${String.format("%.1f", percentageSpent)}% of your total budget.",
                AlertSeverity.CRITICAL
            )
            percentageSpent >= 90 -> BudgetAlert(
                "Warning: You've spent ${String.format("%.1f", percentageSpent)}% of your budget.",
                AlertSeverity.WARNING
            )
            percentageSpent >= 80 -> BudgetAlert(
                "You've spent ${String.format("%.1f", percentageSpent)}% of your budget.",
                AlertSeverity.INFO
            )
            else -> null
        }
    }
    
    private fun calculateCategoryAlerts(items: List<BudgetItem>): Map<BudgetCategory, String> {
        val alerts = mutableMapOf<BudgetCategory, String>()
        
        items.groupBy { it.category }.forEach { (category, categoryItems) ->
            categoryItems.forEach { item ->
                if (item.actualCost > item.estimatedCost && item.estimatedCost > 0) {
                    val overage = item.actualCost - item.estimatedCost
                    alerts[category] = "Over budget by ${String.format("%.2f", overage)}"
                }
            }
        }
        
        return alerts
    }
}

data class AddBudgetItemUiState(
    val name: String = "",
    val category: BudgetCategory = BudgetCategory.OTHER,
    val estimatedCost: String = "",
    val actualCost: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val currency: Currency = Currency.USD
)

@HiltViewModel
class AddBudgetItemViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val preferencesDataStore: PreferencesDataStore
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddBudgetItemUiState())
    val uiState: StateFlow<AddBudgetItemUiState> = _uiState.asStateFlow()
    
    init {
        viewModelScope.launch {
            preferencesDataStore.selectedCurrency.collect { code ->
                _uiState.update { it.copy(currency = Currency.fromCode(code)) }
            }
        }
    }
    
    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }
    
    fun updateCategory(category: BudgetCategory) {
        _uiState.update { it.copy(category = category) }
    }
    
    fun updateEstimatedCost(cost: String) {
        if (cost.isEmpty() || cost.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(estimatedCost = cost) }
        }
    }
    
    fun updateActualCost(cost: String) {
        if (cost.isEmpty() || cost.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(actualCost = cost) }
        }
    }
    
    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }
    
    fun canSave(): Boolean = _uiState.value.name.isNotBlank()
    
    fun saveItem() {
        if (!canSave()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val item = BudgetItem(
                    name = _uiState.value.name.trim(),
                    category = _uiState.value.category,
                    estimatedCost = _uiState.value.estimatedCost.toDoubleOrNull() ?: 0.0,
                    actualCost = _uiState.value.actualCost.toDoubleOrNull() ?: 0.0,
                    notes = _uiState.value.notes.trim()
                )
                budgetRepository.insertBudgetItem(item)
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}

@HiltViewModel
class BudgetDetailViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val preferencesDataStore: PreferencesDataStore
) : ViewModel() {
    
    private val _item = MutableStateFlow<BudgetItem?>(null)
    val item: StateFlow<BudgetItem?> = _item.asStateFlow()
    
    private val _isDeleted = MutableStateFlow(false)
    val isDeleted: StateFlow<Boolean> = _isDeleted.asStateFlow()
    
    private val _currency = MutableStateFlow(Currency.USD)
    val currency: StateFlow<Currency> = _currency.asStateFlow()
    
    init {
        viewModelScope.launch {
            preferencesDataStore.selectedCurrency.collect { code ->
                _currency.value = Currency.fromCode(code)
            }
        }
    }
    
    fun loadItem(itemId: Long) {
        viewModelScope.launch {
            _item.value = budgetRepository.getBudgetItemById(itemId)
        }
    }
    
    fun updateItem(item: BudgetItem) {
        viewModelScope.launch {
            budgetRepository.updateBudgetItem(item)
            _item.value = item
        }
    }
    
    fun deleteItem() {
        _item.value?.let { item ->
            viewModelScope.launch {
                budgetRepository.deleteBudgetItem(item)
                _isDeleted.value = true
            }
        }
    }
}
