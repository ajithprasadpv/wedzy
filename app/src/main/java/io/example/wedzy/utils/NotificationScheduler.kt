package io.example.wedzy.utils

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import io.example.wedzy.workers.TaskReminderWorker
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    
    fun scheduleDailyTaskReminders(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<TaskReminderWorker>(
            24, TimeUnit.HOURS
        ).build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            TaskReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    
    fun cancelTaskReminders(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(TaskReminderWorker.WORK_NAME)
    }
}
