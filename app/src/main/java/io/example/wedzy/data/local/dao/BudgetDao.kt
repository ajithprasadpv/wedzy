package io.example.wedzy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.example.wedzy.data.model.BudgetCategory
import io.example.wedzy.data.model.BudgetItem
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    
    @Query("SELECT * FROM budget_items WHERE userId = :userId ORDER BY category, name")
    fun getAllBudgetItems(userId: String): Flow<List<BudgetItem>>
    
    @Query("SELECT * FROM budget_items WHERE userId = :userId AND category = :category ORDER BY name")
    fun getBudgetItemsByCategory(userId: String, category: BudgetCategory): Flow<List<BudgetItem>>
    
    @Query("SELECT * FROM budget_items WHERE id = :id")
    suspend fun getBudgetItemById(id: Long): BudgetItem?
    
    @Query("SELECT SUM(estimatedCost) FROM budget_items WHERE userId = :userId")
    fun getTotalEstimatedCost(userId: String): Flow<Double?>
    
    @Query("SELECT SUM(actualCost) FROM budget_items WHERE userId = :userId")
    fun getTotalActualCost(userId: String): Flow<Double?>
    
    @Query("SELECT SUM(paidAmount) FROM budget_items WHERE userId = :userId")
    fun getTotalPaidAmount(userId: String): Flow<Double?>
    
    @Query("SELECT SUM(estimatedCost) FROM budget_items WHERE userId = :userId AND category = :category")
    fun getEstimatedCostByCategory(userId: String, category: BudgetCategory): Flow<Double?>
    
    @Query("SELECT SUM(actualCost) FROM budget_items WHERE userId = :userId AND category = :category")
    fun getActualCostByCategory(userId: String, category: BudgetCategory): Flow<Double?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgetItem(item: BudgetItem): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgetItems(items: List<BudgetItem>)
    
    @Update
    suspend fun updateBudgetItem(item: BudgetItem)
    
    @Delete
    suspend fun deleteBudgetItem(item: BudgetItem)
    
    @Query("DELETE FROM budget_items WHERE id = :id")
    suspend fun deleteBudgetItemById(id: Long)
    
    @Query("DELETE FROM budget_items WHERE userId = :userId")
    suspend fun deleteAllBudgetItems(userId: String)
}
