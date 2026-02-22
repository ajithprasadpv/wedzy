package io.example.wedzy.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.example.wedzy.data.model.EventType
import io.example.wedzy.data.model.WeddingEvent
import io.example.wedzy.data.repository.WeddingEventRepository
import io.example.wedzy.notifications.EventNotificationScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class CalendarUiState(
    val events: List<WeddingEvent> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val eventsForSelectedDate: List<WeddingEvent> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val weddingEventRepository: WeddingEventRepository,
    private val notificationScheduler: EventNotificationScheduler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()
    
    init {
        loadEvents()
    }
    
    private fun loadEvents() {
        viewModelScope.launch {
            weddingEventRepository.getAllEvents().collect { events ->
                _uiState.update { state ->
                    state.copy(
                        events = events,
                        eventsForSelectedDate = filterEventsForDate(events, state.selectedDate),
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun selectDate(date: LocalDate) {
        _uiState.update { state ->
            state.copy(
                selectedDate = date,
                eventsForSelectedDate = filterEventsForDate(state.events, date)
            )
        }
    }
    
    private fun filterEventsForDate(events: List<WeddingEvent>, date: LocalDate): List<WeddingEvent> {
        return events.filter { event ->
            val eventDate = Instant.ofEpochMilli(event.startDateTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            eventDate == date
        }
    }
    
    fun deleteEvent(event: WeddingEvent) {
        viewModelScope.launch {
            // Cancel scheduled notifications
            notificationScheduler.cancelNotificationsForEvent(event.id)
            weddingEventRepository.deleteEvent(event)
        }
    }
}

data class AddEventUiState(
    val title: String = "",
    val description: String = "",
    val eventType: EventType = EventType.OTHER,
    val startDate: Long = System.currentTimeMillis(),
    val startTime: String = "10:00",
    val endTime: String = "11:00",
    val location: String = "",
    val notes: String = "",
    val isAllDay: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddEventViewModel @Inject constructor(
    private val weddingEventRepository: WeddingEventRepository,
    private val notificationScheduler: EventNotificationScheduler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddEventUiState())
    val uiState: StateFlow<AddEventUiState> = _uiState.asStateFlow()
    
    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }
    
    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }
    
    fun updateEventType(eventType: EventType) {
        _uiState.update { it.copy(eventType = eventType) }
    }
    
    fun updateStartDate(date: Long) {
        _uiState.update { it.copy(startDate = date) }
    }
    
    fun updateStartTime(time: String) {
        _uiState.update { it.copy(startTime = time) }
    }
    
    fun updateEndTime(time: String) {
        _uiState.update { it.copy(endTime = time) }
    }
    
    fun updateLocation(location: String) {
        _uiState.update { it.copy(location = location) }
    }
    
    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }
    
    fun updateIsAllDay(isAllDay: Boolean) {
        _uiState.update { it.copy(isAllDay = isAllDay) }
    }
    
    fun canSave(): Boolean = _uiState.value.title.isNotBlank()
    
    fun saveEvent() {
        if (!canSave()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val state = _uiState.value
                
                // Combine date and time
                val startDateTime = if (state.isAllDay) {
                    state.startDate
                } else {
                    combineDateTime(state.startDate, state.startTime)
                }
                
                val endDateTime = if (state.isAllDay) {
                    null
                } else {
                    combineDateTime(state.startDate, state.endTime)
                }
                
                val event = WeddingEvent(
                    title = state.title.trim(),
                    description = state.description.trim(),
                    eventType = state.eventType,
                    startDateTime = startDateTime,
                    endDateTime = endDateTime,
                    location = state.location.trim(),
                    notes = state.notes.trim(),
                    isAllDay = state.isAllDay
                )
                val eventId = weddingEventRepository.insertEvent(event)
                
                // Schedule notifications for the event
                val savedEvent = event.copy(id = eventId)
                notificationScheduler.scheduleNotificationsForEvent(savedEvent)
                
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
    
    private fun combineDateTime(dateMillis: Long, timeString: String): Long {
        return try {
            val parts = timeString.split(":")
            val hours = parts.getOrNull(0)?.toIntOrNull() ?: 10
            val minutes = parts.getOrNull(1)?.toIntOrNull() ?: 0
            
            val dateInstant = Instant.ofEpochMilli(dateMillis)
            val localDate = dateInstant.atZone(ZoneId.systemDefault()).toLocalDate()
            
            localDate.atTime(hours, minutes)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        } catch (e: Exception) {
            dateMillis
        }
    }
}
