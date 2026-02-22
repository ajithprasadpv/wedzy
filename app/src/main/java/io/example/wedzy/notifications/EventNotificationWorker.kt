package io.example.wedzy.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class EventNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        val eventId = inputData.getLong(KEY_EVENT_ID, -1)
        val eventTitle = inputData.getString(KEY_EVENT_TITLE) ?: "Wedding Event"
        val eventTime = inputData.getString(KEY_EVENT_TIME) ?: ""
        val eventLocation = inputData.getString(KEY_EVENT_LOCATION) ?: ""
        val notificationTypeOrdinal = inputData.getInt(KEY_NOTIFICATION_TYPE, 0)
        val notificationType = NotificationType.entries.getOrNull(notificationTypeOrdinal) 
            ?: NotificationType.MORNING_OF
        
        if (eventId == -1L) {
            return Result.failure()
        }
        
        val (title, message) = when (notificationType) {
            NotificationType.DAY_BEFORE -> {
                "Tomorrow: $eventTitle" to buildString {
                    append("Don't forget! $eventTitle is tomorrow")
                    if (eventTime.isNotEmpty()) append(" at $eventTime")
                    if (eventLocation.isNotEmpty()) append(" • $eventLocation")
                }
            }
            NotificationType.MORNING_OF -> {
                "Today: $eventTitle" to buildString {
                    append("$eventTitle is today!")
                    if (eventTime.isNotEmpty()) append(" at $eventTime")
                    if (eventLocation.isNotEmpty()) append(" • $eventLocation")
                }
            }
            NotificationType.THIRTY_MINUTES -> {
                "Starting Soon: $eventTitle" to buildString {
                    append("$eventTitle starts in 30 minutes")
                    if (eventLocation.isNotEmpty()) append(" • $eventLocation")
                }
            }
        }
        
        NotificationHelper.showEventNotification(
            context = context,
            eventId = eventId,
            title = title,
            message = message,
            notificationType = notificationType
        )
        
        return Result.success()
    }
    
    companion object {
        const val KEY_EVENT_ID = "event_id"
        const val KEY_EVENT_TITLE = "event_title"
        const val KEY_EVENT_TIME = "event_time"
        const val KEY_EVENT_LOCATION = "event_location"
        const val KEY_NOTIFICATION_TYPE = "notification_type"
    }
}
