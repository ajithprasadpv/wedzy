package io.example.wedzy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.example.wedzy.data.model.Task
import io.example.wedzy.data.model.TaskCategory
import io.example.wedzy.data.model.TaskStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    
    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY dueDate ASC, priority DESC")
    fun getAllTasks(userId: String): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE userId = :userId AND status != :completedStatus ORDER BY dueDate ASC, priority DESC")
    fun getActiveTasks(userId: String, completedStatus: TaskStatus = TaskStatus.COMPLETED): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE userId = :userId AND status = :status ORDER BY dueDate ASC")
    fun getTasksByStatus(userId: String, status: TaskStatus): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE userId = :userId AND category = :category ORDER BY dueDate ASC")
    fun getTasksByCategory(userId: String, category: TaskCategory): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE userId = :userId AND dueDate BETWEEN :startDate AND :endDate ORDER BY dueDate ASC")
    fun getTasksByDateRange(userId: String, startDate: Long, endDate: Long): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE userId = :userId AND dueDate <= :date AND status != :completedStatus ORDER BY dueDate ASC")
    fun getOverdueTasks(userId: String, date: Long, completedStatus: TaskStatus = TaskStatus.COMPLETED): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?
    
    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId")
    fun getTaskCount(userId: String): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND status = :status")
    fun getTaskCountByStatus(userId: String, status: TaskStatus): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)
    
    @Update
    suspend fun updateTask(task: Task)
    
    @Delete
    suspend fun deleteTask(task: Task)
    
    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)
    
    @Query("DELETE FROM tasks WHERE userId = :userId")
    suspend fun deleteAllTasks(userId: String)
}
