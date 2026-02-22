package io.example.wedzy.ui.vendors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.example.wedzy.data.model.Vendor
import io.example.wedzy.data.model.VendorCategory
import io.example.wedzy.data.model.VendorStatus
import io.example.wedzy.data.repository.VendorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VendorsUiState(
    val vendors: List<Vendor> = emptyList(),
    val filteredVendors: List<Vendor> = emptyList(),
    val selectedCategory: VendorCategory? = null,
    val totalVendors: Int = 0,
    val bookedVendors: Int = 0,
    val pendingVendors: Int = 0,
    val rejectedVendors: Int = 0,
    val totalAmount: Double = 0.0,
    val paidAmount: Double = 0.0,
    val pendingAmount: Double = 0.0,
    val isLoading: Boolean = true
)

@HiltViewModel
class VendorsViewModel @Inject constructor(
    private val vendorRepository: VendorRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(VendorsUiState())
    val uiState: StateFlow<VendorsUiState> = _uiState.asStateFlow()
    
    init {
        loadVendors()
    }
    
    private fun loadVendors() {
        viewModelScope.launch {
            vendorRepository.getAllVendors().collect { vendors ->
                val booked = vendors.count { 
                    it.status in listOf(VendorStatus.BOOKED, VendorStatus.DEPOSIT_PAID, VendorStatus.COMPLETED) 
                }
                val pending = vendors.count { 
                    it.status in listOf(VendorStatus.CONTACTED, VendorStatus.MEETING_SCHEDULED, VendorStatus.PROPOSAL_RECEIVED, VendorStatus.RESEARCHING) 
                }
                val rejected = vendors.count { it.status == VendorStatus.CANCELLED }
                
                val totalAmount = vendors.sumOf { it.agreedPrice.takeIf { p -> p > 0 } ?: it.quotedPrice }
                val paidAmount = vendors.filter { it.depositPaid }.sumOf { it.depositAmount }
                val pendingAmount = totalAmount - paidAmount
                
                _uiState.update { state ->
                    state.copy(
                        vendors = vendors,
                        filteredVendors = filterVendors(vendors, state.selectedCategory),
                        totalVendors = vendors.size,
                        bookedVendors = booked,
                        pendingVendors = pending,
                        rejectedVendors = rejected,
                        totalAmount = totalAmount,
                        paidAmount = paidAmount,
                        pendingAmount = pendingAmount,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun setCategory(category: VendorCategory?) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
                filteredVendors = filterVendors(state.vendors, category)
            )
        }
    }
    
    private fun filterVendors(vendors: List<Vendor>, category: VendorCategory?): List<Vendor> {
        return if (category == null) vendors else vendors.filter { it.category == category }
    }
    
    fun deleteVendor(vendor: Vendor) {
        viewModelScope.launch {
            vendorRepository.deleteVendor(vendor)
        }
    }
    
    fun addVendorFromContact(name: String, phone: String, email: String, category: VendorCategory? = null) {
        viewModelScope.launch {
            val vendorCategory = category ?: _uiState.value.selectedCategory ?: VendorCategory.OTHER
            val vendor = Vendor(
                name = name,
                category = vendorCategory,
                contactPerson = name,
                phone = phone,
                email = email
            )
            vendorRepository.insertVendor(vendor)
        }
    }
}

data class AddVendorUiState(
    val name: String = "",
    val category: VendorCategory = VendorCategory.OTHER,
    val contactPerson: String = "",
    val email: String = "",
    val phone: String = "",
    val website: String = "",
    val address: String = "",
    val quotedPrice: String = "",
    val status: VendorStatus = VendorStatus.RESEARCHING,
    val addToBudget: Boolean = false,
    val notes: String = "",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddVendorViewModel @Inject constructor(
    private val vendorRepository: VendorRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddVendorUiState())
    val uiState: StateFlow<AddVendorUiState> = _uiState.asStateFlow()
    
    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }
    
    fun updateCategory(category: VendorCategory) {
        _uiState.update { it.copy(category = category) }
    }
    
    fun updateContactPerson(contactPerson: String) {
        _uiState.update { it.copy(contactPerson = contactPerson) }
    }
    
    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }
    
    fun updatePhone(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }
    
    fun updateWebsite(website: String) {
        _uiState.update { it.copy(website = website) }
    }
    
    fun updateAddress(address: String) {
        _uiState.update { it.copy(address = address) }
    }
    
    fun updateQuotedPrice(price: String) {
        if (price.isEmpty() || price.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(quotedPrice = price) }
        }
    }
    
    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }
    
    fun updateStatus(status: VendorStatus) {
        _uiState.update { it.copy(status = status) }
    }
    
    fun updateAddToBudget(addToBudget: Boolean) {
        _uiState.update { it.copy(addToBudget = addToBudget) }
    }
    
    fun canSave(): Boolean = _uiState.value.name.isNotBlank()
    
    fun saveVendor() {
        if (!canSave()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val vendor = Vendor(
                    name = _uiState.value.name.trim(),
                    category = _uiState.value.category,
                    contactPerson = _uiState.value.contactPerson.trim(),
                    email = _uiState.value.email.trim(),
                    phone = _uiState.value.phone.trim(),
                    website = _uiState.value.website.trim(),
                    address = _uiState.value.address.trim(),
                    quotedPrice = _uiState.value.quotedPrice.toDoubleOrNull() ?: 0.0,
                    status = _uiState.value.status,
                    notes = _uiState.value.notes.trim()
                )
                vendorRepository.insertVendor(vendor)
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}

@HiltViewModel
class VendorDetailViewModel @Inject constructor(
    private val vendorRepository: VendorRepository
) : ViewModel() {
    
    private val _vendor = MutableStateFlow<Vendor?>(null)
    val vendor: StateFlow<Vendor?> = _vendor.asStateFlow()
    
    private val _isDeleted = MutableStateFlow(false)
    val isDeleted: StateFlow<Boolean> = _isDeleted.asStateFlow()
    
    fun loadVendor(vendorId: Long) {
        viewModelScope.launch {
            _vendor.value = vendorRepository.getVendorById(vendorId)
        }
    }
    
    fun updateStatus(status: VendorStatus) {
        _vendor.value?.let { vendor ->
            viewModelScope.launch {
                vendorRepository.updateVendorStatus(vendor, status)
                _vendor.value = vendor.copy(status = status)
            }
        }
    }
    
    fun updateVendor(vendor: Vendor) {
        viewModelScope.launch {
            vendorRepository.updateVendor(vendor)
            _vendor.value = vendor
        }
    }
    
    fun deleteVendor() {
        _vendor.value?.let { vendor ->
            viewModelScope.launch {
                vendorRepository.deleteVendor(vendor)
                _isDeleted.value = true
            }
        }
    }
}
