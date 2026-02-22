package io.example.wedzy.data.repository

import io.example.wedzy.data.auth.UserSession
import io.example.wedzy.data.firebase.FirestoreSyncRepository
import io.example.wedzy.data.local.dao.VendorDao
import io.example.wedzy.data.model.Vendor
import io.example.wedzy.data.model.VendorCategory
import io.example.wedzy.data.model.VendorStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VendorRepository @Inject constructor(
    private val vendorDao: VendorDao,
    private val userSession: UserSession,
    private val sync: FirestoreSyncRepository
) {
    fun getAllVendors(): Flow<List<Vendor>> {
        val userId = userSession.getCurrentUserId()
        return vendorDao.getAllVendors(userId)
    }
    
    fun getVendorsByCategory(category: VendorCategory): Flow<List<Vendor>> {
        val userId = userSession.getCurrentUserId()
        return vendorDao.getVendorsByCategory(userId, category)
    }
    
    fun getVendorsByStatus(status: VendorStatus): Flow<List<Vendor>> {
        val userId = userSession.getCurrentUserId()
        return vendorDao.getVendorsByStatus(userId, status)
    }
    
    fun getBookedVendors(): Flow<List<Vendor>> {
        val userId = userSession.getCurrentUserId()
        return vendorDao.getBookedVendors(userId)
    }
    
    suspend fun getVendorById(id: Long): Vendor? = vendorDao.getVendorById(id)
    
    fun getVendorCount(): Flow<Int> {
        val userId = userSession.getCurrentUserId()
        return vendorDao.getVendorCount(userId)
    }
    
    fun getVendorCountByStatus(status: VendorStatus): Flow<Int> {
        val userId = userSession.getCurrentUserId()
        return vendorDao.getVendorCountByStatus(userId, status)
    }
    
    fun getTotalBookedAmount(): Flow<Double> {
        val userId = userSession.getCurrentUserId()
        return vendorDao.getTotalBookedAmount(userId).map { it ?: 0.0 }
    }
    
    fun getTotalDepositsPaid(): Flow<Double> {
        val userId = userSession.getCurrentUserId()
        return vendorDao.getTotalDepositsPaid(userId).map { it ?: 0.0 }
    }
    
    suspend fun insertVendor(vendor: Vendor): Long {
        val userId = userSession.getCurrentUserId()
        val withUser = vendor.copy(userId = userId)
        val id = vendorDao.insertVendor(withUser)
        sync.syncVendorToCloud(withUser.copy(id = id))
        return id
    }
    
    suspend fun insertVendors(vendors: List<Vendor>) {
        val userId = userSession.getCurrentUserId()
        val vendorsWithUserId = vendors.map { it.copy(userId = userId) }
        vendorDao.insertVendors(vendorsWithUserId)
        vendorsWithUserId.forEach { sync.syncVendorToCloud(it) }
    }
    
    suspend fun updateVendor(vendor: Vendor) {
        val userId = userSession.getCurrentUserId()
        val withUser = vendor.copy(userId = userId)
        vendorDao.updateVendor(withUser)
        sync.syncVendorToCloud(withUser)
    }
    
    suspend fun updateVendorStatus(vendor: Vendor, status: VendorStatus) {
        val userId = userSession.getCurrentUserId()
        val updated = vendor.copy(userId = userId, status = status)
        vendorDao.updateVendor(updated)
        sync.syncVendorToCloud(updated)
    }
    
    suspend fun deleteVendor(vendor: Vendor) {
        vendorDao.deleteVendor(vendor)
        sync.deleteVendorFromCloud(vendor.id)
    }
    
    suspend fun deleteVendorById(id: Long) {
        vendorDao.deleteVendorById(id)
        sync.deleteVendorFromCloud(id)
    }
    
    suspend fun clearUserData() {
        val userId = userSession.getCurrentUserId()
        if (userId.isNotEmpty()) {
            vendorDao.deleteAllVendors(userId)
        }
    }
}
