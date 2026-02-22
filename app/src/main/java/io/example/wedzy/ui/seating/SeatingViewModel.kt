package io.example.wedzy.ui.seating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.example.wedzy.data.model.Guest
import io.example.wedzy.data.model.SeatAssignment
import io.example.wedzy.data.model.SeatingTable
import io.example.wedzy.data.model.TableShape
import io.example.wedzy.data.repository.GuestRepository
import io.example.wedzy.data.repository.SeatingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TableWithGuests(
    val table: SeatingTable,
    val guests: List<Guest>,
    val availableSeats: Int
)

data class SeatingUiState(
    val tables: List<TableWithGuests> = emptyList(),
    val unseatedGuests: List<Guest> = emptyList(),
    val totalTables: Int = 0,
    val totalSeated: Int = 0,
    val totalGuests: Int = 0,
    val selectedTable: SeatingTable? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class SeatingViewModel @Inject constructor(
    private val seatingRepository: SeatingRepository,
    private val guestRepository: GuestRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SeatingUiState())
    val uiState: StateFlow<SeatingUiState> = _uiState.asStateFlow()
    
    init {
        loadSeatingData()
    }
    
    private fun loadSeatingData() {
        viewModelScope.launch {
            combine(
                seatingRepository.getAllTables(),
                seatingRepository.getAllSeatAssignments(),
                guestRepository.getAllGuests()
            ) { tables, assignments, guests ->
                val tablesWithGuests = tables.map { table ->
                    val tableAssignments = assignments.filter { it.tableId == table.id }
                    val seatedGuests = tableAssignments.mapNotNull { assignment ->
                        guests.find { it.id == assignment.guestId }
                    }
                    TableWithGuests(
                        table = table,
                        guests = seatedGuests,
                        availableSeats = table.capacity - seatedGuests.size
                    )
                }
                
                val seatedGuestIds = assignments.map { it.guestId }.toSet()
                val unseated = guests.filter { it.id !in seatedGuestIds }
                
                SeatingUiState(
                    tables = tablesWithGuests,
                    unseatedGuests = unseated,
                    totalTables = tables.size,
                    totalSeated = assignments.size,
                    totalGuests = guests.size,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.update { state }
            }
        }
    }
    
    fun selectTable(table: SeatingTable?) {
        _uiState.update { it.copy(selectedTable = table) }
    }
    
    fun addTable(name: String, capacity: Int, shape: TableShape) {
        viewModelScope.launch {
            val tableNumber = (_uiState.value.totalTables + 1)
            val table = SeatingTable(
                name = name.ifBlank { "Table $tableNumber" },
                tableNumber = tableNumber,
                capacity = capacity,
                shape = shape
            )
            seatingRepository.insertTable(table)
        }
    }
    
    fun deleteTable(table: SeatingTable) {
        viewModelScope.launch {
            seatingRepository.clearTable(table.id)
            seatingRepository.deleteTable(table)
        }
    }
    
    fun assignGuestToTable(guest: Guest, table: SeatingTable) {
        viewModelScope.launch {
            seatingRepository.removeGuestFromSeat(guest.id)
            val assignment = SeatAssignment(
                tableId = table.id,
                guestId = guest.id
            )
            seatingRepository.assignSeat(assignment)
        }
    }
    
    fun removeGuestFromTable(guest: Guest) {
        viewModelScope.launch {
            seatingRepository.removeGuestFromSeat(guest.id)
        }
    }
}
