package io.example.wedzy.ui.guests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.example.wedzy.data.model.Guest
import io.example.wedzy.data.model.GuestRelation
import io.example.wedzy.data.model.GuestSide
import io.example.wedzy.data.model.RsvpStatus
import io.example.wedzy.data.repository.GuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GuestsUiState(
    val guests: List<Guest> = emptyList(),
    val filteredGuests: List<Guest> = emptyList(),
    val selectedFilter: RsvpFilter = RsvpFilter.ALL,
    val totalGuests: Int = 0,
    val confirmedGuests: Int = 0,
    val pendingGuests: Int = 0,
    val declinedGuests: Int = 0,
    val isLoading: Boolean = true
)

enum class RsvpFilter {
    ALL, CONFIRMED, PENDING, DECLINED, INVITED
}

@HiltViewModel
class GuestsViewModel @Inject constructor(
    private val guestRepository: GuestRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GuestsUiState())
    val uiState: StateFlow<GuestsUiState> = _uiState.asStateFlow()
    
    init {
        loadGuests()
    }
    
    private fun loadGuests() {
        viewModelScope.launch {
            guestRepository.getAllGuests().collect { guests ->
                val confirmed = guests.count { it.rsvpStatus == RsvpStatus.CONFIRMED }
                val pending = guests.count { it.rsvpStatus == RsvpStatus.PENDING }
                val declined = guests.count { it.rsvpStatus == RsvpStatus.DECLINED }
                
                _uiState.update { state ->
                    state.copy(
                        guests = guests,
                        filteredGuests = filterGuests(guests, state.selectedFilter),
                        totalGuests = guests.size,
                        confirmedGuests = confirmed,
                        pendingGuests = pending,
                        declinedGuests = declined,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun setFilter(filter: RsvpFilter) {
        _uiState.update { state ->
            state.copy(
                selectedFilter = filter,
                filteredGuests = filterGuests(state.guests, filter)
            )
        }
    }
    
    private fun filterGuests(guests: List<Guest>, filter: RsvpFilter): List<Guest> {
        return when (filter) {
            RsvpFilter.ALL -> guests
            RsvpFilter.CONFIRMED -> guests.filter { it.rsvpStatus == RsvpStatus.CONFIRMED }
            RsvpFilter.PENDING -> guests.filter { it.rsvpStatus == RsvpStatus.PENDING }
            RsvpFilter.DECLINED -> guests.filter { it.rsvpStatus == RsvpStatus.DECLINED }
            RsvpFilter.INVITED -> guests.filter { it.rsvpStatus == RsvpStatus.INVITED }
        }
    }
    
    fun updateRsvpStatus(guest: Guest, status: RsvpStatus) {
        viewModelScope.launch {
            guestRepository.updateRsvpStatus(guest, status)
        }
    }
    
    fun deleteGuest(guest: Guest) {
        viewModelScope.launch {
            guestRepository.deleteGuest(guest)
        }
    }
    
    fun addGuestsFromContacts(
        contacts: List<Pair<String, String?>>,
        side: GuestSide = GuestSide.MUTUAL
    ) {
        viewModelScope.launch {
            contacts.forEach { (name, phone) ->
                val nameParts = name.split(" ", limit = 2)
                val guest = Guest(
                    firstName = nameParts.firstOrNull() ?: name,
                    lastName = nameParts.getOrNull(1) ?: "",
                    phone = phone ?: "",
                    side = side,
                    rsvpStatus = RsvpStatus.PENDING
                )
                guestRepository.insertGuest(guest)
            }
        }
    }
}

data class AddGuestUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val side: GuestSide = GuestSide.MUTUAL,
    val relation: GuestRelation = GuestRelation.OTHER,
    val plusOneAllowed: Boolean = false,
    val dietaryRestrictions: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddGuestViewModel @Inject constructor(
    private val guestRepository: GuestRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddGuestUiState())
    val uiState: StateFlow<AddGuestUiState> = _uiState.asStateFlow()
    
    fun updateFirstName(name: String) {
        _uiState.update { it.copy(firstName = name) }
    }
    
    fun updateLastName(name: String) {
        _uiState.update { it.copy(lastName = name) }
    }
    
    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }
    
    fun updatePhone(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }
    
    fun updateSide(side: GuestSide) {
        _uiState.update { it.copy(side = side) }
    }
    
    fun updateRelation(relation: GuestRelation) {
        _uiState.update { it.copy(relation = relation) }
    }
    
    fun updatePlusOneAllowed(allowed: Boolean) {
        _uiState.update { it.copy(plusOneAllowed = allowed) }
    }
    
    fun updateDietaryRestrictions(restrictions: String) {
        _uiState.update { it.copy(dietaryRestrictions = restrictions) }
    }
    
    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }
    
    fun canSave(): Boolean = _uiState.value.firstName.isNotBlank()
    
    fun saveGuest() {
        if (!canSave()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val guest = Guest(
                    firstName = _uiState.value.firstName.trim(),
                    lastName = _uiState.value.lastName.trim(),
                    email = _uiState.value.email.trim(),
                    phone = _uiState.value.phone.trim(),
                    side = _uiState.value.side,
                    relation = _uiState.value.relation,
                    plusOneAllowed = _uiState.value.plusOneAllowed,
                    dietaryRestrictions = _uiState.value.dietaryRestrictions.trim(),
                    notes = _uiState.value.notes.trim()
                )
                guestRepository.insertGuest(guest)
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}

@HiltViewModel
class GuestDetailViewModel @Inject constructor(
    private val guestRepository: GuestRepository
) : ViewModel() {
    
    private val _guest = MutableStateFlow<Guest?>(null)
    val guest: StateFlow<Guest?> = _guest.asStateFlow()
    
    private val _isDeleted = MutableStateFlow(false)
    val isDeleted: StateFlow<Boolean> = _isDeleted.asStateFlow()
    
    fun loadGuest(guestId: Long) {
        viewModelScope.launch {
            _guest.value = guestRepository.getGuestById(guestId)
        }
    }
    
    fun updateRsvpStatus(status: RsvpStatus) {
        _guest.value?.let { guest ->
            viewModelScope.launch {
                guestRepository.updateRsvpStatus(guest, status)
                _guest.value = guest.copy(rsvpStatus = status)
            }
        }
    }
    
    fun updateGuest(guest: Guest) {
        viewModelScope.launch {
            guestRepository.updateGuest(guest)
            _guest.value = guest
        }
    }
    
    fun deleteGuest() {
        _guest.value?.let { guest ->
            viewModelScope.launch {
                guestRepository.deleteGuest(guest)
                _isDeleted.value = true
            }
        }
    }
}
