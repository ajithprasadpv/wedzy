package io.example.wedzy.data.repository

import io.example.wedzy.data.auth.UserSession
import io.example.wedzy.data.firebase.FirestoreSyncRepository
import io.example.wedzy.data.local.dao.BudgetDao
import io.example.wedzy.data.model.BudgetCategory
import io.example.wedzy.data.model.BudgetItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao,
    private val userSession: UserSession,
    private val sync: FirestoreSyncRepository
) {
    fun getAllBudgetItems(): Flow<List<BudgetItem>> {
        val userId = userSession.getCurrentUserId()
        return budgetDao.getAllBudgetItems(userId)
    }
    
    fun getBudgetItemsByCategory(category: BudgetCategory): Flow<List<BudgetItem>> {
        val userId = userSession.getCurrentUserId()
        return budgetDao.getBudgetItemsByCategory(userId, category)
    }
    
    suspend fun getBudgetItemById(id: Long): BudgetItem? = budgetDao.getBudgetItemById(id)
    
    fun getTotalEstimatedCost(): Flow<Double> {
        val userId = userSession.getCurrentUserId()
        return budgetDao.getTotalEstimatedCost(userId).map { it ?: 0.0 }
    }
    
    fun getTotalActualCost(): Flow<Double> {
        val userId = userSession.getCurrentUserId()
        return budgetDao.getTotalActualCost(userId).map { it ?: 0.0 }
    }
    
    fun getTotalPaidAmount(): Flow<Double> {
        val userId = userSession.getCurrentUserId()
        return budgetDao.getTotalPaidAmount(userId).map { it ?: 0.0 }
    }
    
    fun getEstimatedCostByCategory(category: BudgetCategory): Flow<Double> {
        val userId = userSession.getCurrentUserId()
        return budgetDao.getEstimatedCostByCategory(userId, category).map { it ?: 0.0 }
    }
    
    fun getActualCostByCategory(category: BudgetCategory): Flow<Double> {
        val userId = userSession.getCurrentUserId()
        return budgetDao.getActualCostByCategory(userId, category).map { it ?: 0.0 }
    }
    
    suspend fun insertBudgetItem(item: BudgetItem): Long {
        val userId = userSession.getCurrentUserId()
        val withUser = item.copy(userId = userId)
        val id = budgetDao.insertBudgetItem(withUser)
        sync.syncBudgetItemToCloud(withUser.copy(id = id))
        return id
    }
    
    suspend fun insertBudgetItems(items: List<BudgetItem>) {
        val userId = userSession.getCurrentUserId()
        val itemsWithUserId = items.map { it.copy(userId = userId) }
        budgetDao.insertBudgetItems(itemsWithUserId)
        itemsWithUserId.forEach { sync.syncBudgetItemToCloud(it) }
    }
    
    suspend fun updateBudgetItem(item: BudgetItem) {
        val userId = userSession.getCurrentUserId()
        val withUser = item.copy(userId = userId)
        budgetDao.updateBudgetItem(withUser)
        sync.syncBudgetItemToCloud(withUser)
    }
    
    suspend fun deleteBudgetItem(item: BudgetItem) {
        budgetDao.deleteBudgetItem(item)
        sync.deleteBudgetItemFromCloud(item.id)
    }
    
    suspend fun deleteBudgetItemById(id: Long) {
        budgetDao.deleteBudgetItemById(id)
        sync.deleteBudgetItemFromCloud(id)
    }
    
    suspend fun clearUserData() {
        val userId = userSession.getCurrentUserId()
        if (userId.isNotEmpty()) {
            budgetDao.deleteAllBudgetItems(userId)
        }
    }
}
