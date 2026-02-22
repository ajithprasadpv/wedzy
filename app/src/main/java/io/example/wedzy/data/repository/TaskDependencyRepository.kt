package io.example.wedzy.data.repository

import io.example.wedzy.data.auth.UserSession
import io.example.wedzy.data.local.dao.TaskDependencyDao
import io.example.wedzy.data.model.TaskDependency
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskDependencyRepository @Inject constructor(
    private val taskDependencyDao: TaskDependencyDao,
    private val userSession: UserSession
) {
    fun getAllDependencies(): Flow<List<TaskDependency>> {
        val userId = userSession.getCurrentUserId()
        return taskDependencyDao.getAllDependencies(userId)
    }
    
    fun getDependenciesForTask(taskId: Long): Flow<List<TaskDependency>> {
        val userId = userSession.getCurrentUserId()
        return taskDependencyDao.getDependenciesForTask(userId, taskId)
    }
    
    fun getDependentTasks(taskId: Long): Flow<List<TaskDependency>> {
        val userId = userSession.getCurrentUserId()
        return taskDependencyDao.getDependentTasks(userId, taskId)
    }
    
    suspend fun insertDependency(dependency: TaskDependency): Long {
        val userId = userSession.getCurrentUserId()
        return taskDependencyDao.insertDependency(dependency.copy(userId = userId))
    }
    
    suspend fun insertDependencies(dependencies: List<TaskDependency>) {
        val userId = userSession.getCurrentUserId()
        val dependenciesWithUserId = dependencies.map { it.copy(userId = userId) }
        taskDependencyDao.insertDependencies(dependenciesWithUserId)
    }
    
    suspend fun deleteDependency(dependency: TaskDependency) {
        taskDependencyDao.deleteDependency(dependency)
    }
    
    suspend fun clearUserData() {
        val userId = userSession.getCurrentUserId()
        if (userId.isNotEmpty()) {
            taskDependencyDao.deleteAllDependencies(userId)
        }
    }
}
