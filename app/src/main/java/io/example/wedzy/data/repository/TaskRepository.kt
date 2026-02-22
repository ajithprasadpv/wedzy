package io.example.wedzy.data.repository

import io.example.wedzy.data.auth.UserSession
import io.example.wedzy.data.firebase.FirestoreSyncRepository
import io.example.wedzy.data.local.dao.TaskDao
import io.example.wedzy.data.model.Task
import io.example.wedzy.data.model.TaskCategory
import io.example.wedzy.data.model.TaskStatus
import io.example.wedzy.data.model.TaskTemplate
import io.example.wedzy.data.model.WeddingTaskTemplates
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val userSession: UserSession,
    private val sync: FirestoreSyncRepository
) {
    fun getAllTasks(): Flow<List<Task>> {
        val userId = userSession.getCurrentUserId()
        return taskDao.getAllTasks(userId)
    }
    
    fun getActiveTasks(): Flow<List<Task>> {
        val userId = userSession.getCurrentUserId()
        return taskDao.getActiveTasks(userId)
    }
    
    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>> {
        val userId = userSession.getCurrentUserId()
        return taskDao.getTasksByStatus(userId, status)
    }
    
    fun getTasksByCategory(category: TaskCategory): Flow<List<Task>> {
        val userId = userSession.getCurrentUserId()
        return taskDao.getTasksByCategory(userId, category)
    }
    
    fun getTasksByDateRange(startDate: Long, endDate: Long): Flow<List<Task>> {
        val userId = userSession.getCurrentUserId()
        return taskDao.getTasksByDateRange(userId, startDate, endDate)
    }
    
    fun getOverdueTasks(): Flow<List<Task>> {
        val userId = userSession.getCurrentUserId()
        return taskDao.getOverdueTasks(userId, System.currentTimeMillis())
    }
    
    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)
    
    fun getTaskCount(): Flow<Int> {
        val userId = userSession.getCurrentUserId()
        return taskDao.getTaskCount(userId)
    }
    
    fun getCompletedTaskCount(): Flow<Int> {
        val userId = userSession.getCurrentUserId()
        return taskDao.getTaskCountByStatus(userId, TaskStatus.COMPLETED)
    }
    
    suspend fun insertTask(task: Task): Long {
        val userId = userSession.getCurrentUserId()
        val withUser = task.copy(userId = userId)
        val id = taskDao.insertTask(withUser)
        sync.syncTaskToCloud(withUser.copy(id = id))
        return id
    }
    
    suspend fun insertTasks(tasks: List<Task>) {
        val userId = userSession.getCurrentUserId()
        val tasksWithUserId = tasks.map { it.copy(userId = userId) }
        taskDao.insertTasks(tasksWithUserId)
        tasksWithUserId.forEach { sync.syncTaskToCloud(it) }
    }
    
    suspend fun updateTask(task: Task) {
        val userId = userSession.getCurrentUserId()
        val withUser = task.copy(userId = userId)
        taskDao.updateTask(withUser)
        sync.syncTaskToCloud(withUser)
    }
    
    suspend fun completeTask(task: Task) {
        val userId = userSession.getCurrentUserId()
        val completed = task.copy(
            userId = userId,
            status = TaskStatus.COMPLETED,
            completedAt = System.currentTimeMillis()
        )
        taskDao.updateTask(completed)
        sync.syncTaskToCloud(completed)
    }
    
    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
        sync.deleteTaskFromCloud(task.id)
    }
    
    suspend fun deleteTaskById(id: Long) {
        taskDao.deleteTaskById(id)
        sync.deleteTaskFromCloud(id)
    }
    
    suspend fun clearUserData() {
        val userId = userSession.getCurrentUserId()
        if (userId.isNotEmpty()) {
            taskDao.deleteAllTasks(userId)
        }
    }
    
    suspend fun generateTasksFromTemplate(weddingDateMillis: Long): Int {
        val userId = userSession.getCurrentUserId()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = weddingDateMillis
        
        val tasks = WeddingTaskTemplates.templates.map { template ->
            val taskCalendar = Calendar.getInstance()
            taskCalendar.timeInMillis = weddingDateMillis
            
            if (template.monthsBeforeWedding > 0) {
                taskCalendar.add(Calendar.MONTH, -template.monthsBeforeWedding)
            } else if (template.estimatedDurationDays > 0) {
                taskCalendar.add(Calendar.DAY_OF_MONTH, -template.estimatedDurationDays)
            }
            
            Task(
                userId = userId,
                title = template.title,
                description = template.description,
                category = template.category,
                priority = template.priority,
                dueDate = taskCalendar.timeInMillis,
                status = TaskStatus.PENDING
            )
        }
        
        taskDao.insertTasks(tasks)
        return tasks.size
    }
}
