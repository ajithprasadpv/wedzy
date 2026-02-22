package io.example.wedzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EventType {
    CEREMONY, RECEPTION, REHEARSAL_DINNER, ENGAGEMENT_PARTY,
    BRIDAL_SHOWER, BACHELOR_PARTY, BACHELORETTE_PARTY,
    WEDDING_SHOWER, WELCOME_PARTY, FAREWELL_BRUNCH,
    VENDOR_MEETING, DRESS_FITTING, CAKE_TASTING,
    VENUE_VISIT, PHOTO_SHOOT, OTHER
}

enum class EventStatus {
    SCHEDULED, CONFIRMED, COMPLETED, CANCELLED
}

@Entity(tableName = "wedding_events")
data class WeddingEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "",
    val title: String,
    val description: String = "",
    val eventType: EventType = EventType.OTHER,
    val status: EventStatus = EventStatus.SCHEDULED,
    val startDateTime: Long, // Unix timestamp
    val endDateTime: Long? = null,
    val location: String = "",
    val address: String = "",
    val notes: String = "",
    val reminderMinutesBefore: Int = 60,
    val isAllDay: Boolean = false,
    val color: String = "#E91E63", // Default rose color
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
