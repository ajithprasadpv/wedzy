package io.example.wedzy.notifications

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.example.wedzy.data.model.WeddingEvent
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventNotificationScheduler @Inject constructor(
    private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)
    
    fun scheduleNotificationsForEvent(event: WeddingEvent) {
        val eventDateTime = Instant.ofEpochMilli(event.startDateTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        
        val eventTimeString = if (!event.isAllDay) {
            String.format("%02d:%02d", eventDateTime.hour, eventDateTime.minute)
        } else ""
        
        // Schedule day-before notification (6 PM the evening before)
        scheduleDayBeforeNotification(event, eventDateTime, eventTimeString)
        
        // Schedule morning-of notification (8 AM on event day)
        scheduleMorningOfNotification(event, eventDateTime, eventTimeString)
        
        // Schedule 30-minutes-before notification (only for timed events)
        if (!event.isAllDay) {
            scheduleThirtyMinutesBeforeNotification(event, eventDateTime)
        }
    }
    
    private fun scheduleDayBeforeNotification(
        event: WeddingEvent,
        eventDateTime: LocalDateTime,
        eventTimeString: String
    ) {
        val dayBefore = eventDateTime.toLocalDate().minusDays(1)
        val notificationTime = LocalDateTime.of(dayBefore, LocalTime.of(18, 0)) // 6 PM
        
        val delayMillis = calculateDelayMillis(notificationTime)
        if (delayMillis <= 0) return // Don't schedule if time has passed
        
        val inputData = createInputData(event, eventTimeString, NotificationType.DAY_BEFORE)
        
        val workRequest = OneTimeWorkRequestBuilder<EventNotificationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()
        
        workManager.enqueueUniqueWork(
            getWorkName(event.id, NotificationType.DAY_BEFORE),
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    private fun scheduleMorningOfNotification(
        event: WeddingEvent,
        eventDateTime: LocalDateTime,
        eventTimeString: String
    ) {
        val eventDate = eventDateTime.toLocalDate()
        val notificationTime = LocalDateTime.of(eventDate, LocalTime.of(8, 0)) // 8 AM
        
        val delayMillis = calculateDelayMillis(notificationTime)
        if (delayMillis <= 0) return // Don't schedule if time has passed
        
        val inputData = createInputData(event, eventTimeString, NotificationType.MORNING_OF)
        
        val workRequest = OneTimeWorkRequestBuilder<EventNotificationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()
        
        workManager.enqueueUniqueWork(
            getWorkName(event.id, NotificationType.MORNING_OF),
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    private fun scheduleThirtyMinutesBeforeNotification(
        event: WeddingEvent,
        eventDateTime: LocalDateTime
    ) {
        val notificationTime = eventDateTime.minusMinutes(30)
        
        val delayMillis = calculateDelayMillis(notificationTime)
        if (delayMillis <= 0) return // Don't schedule if time has passed
        
        val inputData = createInputData(event, "", NotificationType.THIRTY_MINUTES)
        
        val workRequest = OneTimeWorkRequestBuilder<EventNotificationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()
        
        workManager.enqueueUniqueWork(
            getWorkName(event.id, NotificationType.THIRTY_MINUTES),
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    fun cancelNotificationsForEvent(eventId: Long) {
        NotificationType.entries.forEach { type ->
            workManager.cancelUniqueWork(getWorkName(eventId, type))
        }
    }
    
    private fun calculateDelayMillis(targetDateTime: LocalDateTime): Long {
        val now = LocalDateTime.now()
        val targetInstant = targetDateTime.atZone(ZoneId.systemDefault()).toInstant()
        val nowInstant = now.atZone(ZoneId.systemDefault()).toInstant()
        return targetInstant.toEpochMilli() - nowInstant.toEpochMilli()
    }
    
    private fun createInputData(
        event: WeddingEvent,
        eventTimeString: String,
        notificationType: NotificationType
    ): Data {
        return Data.Builder()
            .putLong(EventNotificationWorker.KEY_EVENT_ID, event.id)
            .putString(EventNotificationWorker.KEY_EVENT_TITLE, event.title)
            .putString(EventNotificationWorker.KEY_EVENT_TIME, eventTimeString)
            .putString(EventNotificationWorker.KEY_EVENT_LOCATION, event.location)
            .putInt(EventNotificationWorker.KEY_NOTIFICATION_TYPE, notificationType.ordinal)
            .build()
    }
    
    private fun getWorkName(eventId: Long, type: NotificationType): String {
        return "event_notification_${eventId}_${type.name}"
    }
}
