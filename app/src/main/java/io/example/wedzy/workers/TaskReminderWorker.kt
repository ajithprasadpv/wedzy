package io.example.wedzy.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.example.wedzy.MainActivity
import io.example.wedzy.R
import io.example.wedzy.data.model.TaskStatus
import io.example.wedzy.data.repository.TaskRepository
import kotlinx.coroutines.flow.first
import java.util.Calendar

@HiltWorker
class TaskReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val taskRepository: TaskRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            createNotificationChannel()
            checkAndNotifyTasks()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private suspend fun checkAndNotifyTasks() {
        val tasks = taskRepository.getAllTasks().first()
        val now = System.currentTimeMillis()
        val tomorrow = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis

        val overdueTasks = tasks.filter { task ->
            task.dueDate != null && 
            task.dueDate < now && 
            task.status != TaskStatus.COMPLETED
        }

        val upcomingTasks = tasks.filter { task ->
            task.dueDate != null && 
            task.dueDate in now..tomorrow && 
            task.status != TaskStatus.COMPLETED
        }

        if (overdueTasks.isNotEmpty()) {
            sendNotification(
                "Overdue Tasks",
                "You have ${overdueTasks.size} overdue task(s)",
                OVERDUE_NOTIFICATION_ID
            )
        }

        if (upcomingTasks.isNotEmpty()) {
            sendNotification(
                "Upcoming Tasks",
                "You have ${upcomingTasks.size} task(s) due soon",
                UPCOMING_NOTIFICATION_ID
            )
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Task Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for task reminders and deadlines"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(title: String, message: String, notificationId: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.wedzy_logo)
        builder.setContentTitle(title)
        builder.setContentText(message)
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT)
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)
        
        val notification = builder.build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    companion object {
        const val CHANNEL_ID = "task_reminders"
        const val OVERDUE_NOTIFICATION_ID = 1
        const val UPCOMING_NOTIFICATION_ID = 2
        const val WORK_NAME = "task_reminder_work"
    }
}
