package io.example.wedzy.data.local.dao

import androidx.room.*
import io.example.wedzy.data.model.TaskDependency
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDependencyDao {
    
    @Query("SELECT * FROM task_dependencies WHERE userId = :userId")
    fun getAllDependencies(userId: String): Flow<List<TaskDependency>>
    
    @Query("SELECT * FROM task_dependencies WHERE userId = :userId AND taskId = :taskId")
    fun getDependenciesForTask(userId: String, taskId: Long): Flow<List<TaskDependency>>
    
    @Query("SELECT * FROM task_dependencies WHERE userId = :userId AND dependsOnTaskId = :taskId")
    fun getDependentTasks(userId: String, taskId: Long): Flow<List<TaskDependency>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDependency(dependency: TaskDependency): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDependencies(dependencies: List<TaskDependency>)
    
    @Delete
    suspend fun deleteDependency(dependency: TaskDependency)
    
    @Query("DELETE FROM task_dependencies WHERE userId = :userId")
    suspend fun deleteAllDependencies(userId: String)
}
