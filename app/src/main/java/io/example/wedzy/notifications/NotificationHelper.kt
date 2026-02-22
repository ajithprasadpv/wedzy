package io.example.wedzy.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import io.example.wedzy.MainActivity
import io.example.wedzy.R

object NotificationHelper {
    
    const val CHANNEL_ID = "wedzy_event_reminders"
    const val CHANNEL_NAME = "Event Reminders"
    const val CHANNEL_DESCRIPTION = "Notifications for upcoming wedding events"
    
    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_DESCRIPTION
            enableVibration(true)
            enableLights(true)
        }
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    
    fun showEventNotification(
        context: Context,
        eventId: Long,
        title: String,
        message: String,
        notificationType: NotificationType
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("eventId", eventId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            eventId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notificationId = generateNotificationId(eventId, notificationType)
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.wedzy_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }
    
    private fun generateNotificationId(eventId: Long, type: NotificationType): Int {
        return (eventId * 10 + type.ordinal).toInt()
    }
}

enum class NotificationType {
    DAY_BEFORE,      // Evening before (6 PM)
    MORNING_OF,      // Morning of event day (8 AM)
    THIRTY_MINUTES   // 30 minutes before event time
}
