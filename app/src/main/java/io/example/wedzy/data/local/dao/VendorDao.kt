package io.example.wedzy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.example.wedzy.data.model.Vendor
import io.example.wedzy.data.model.VendorCategory
import io.example.wedzy.data.model.VendorStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface VendorDao {
    
    @Query("SELECT * FROM vendors WHERE userId = :userId ORDER BY category, name")
    fun getAllVendors(userId: String): Flow<List<Vendor>>
    
    @Query("SELECT * FROM vendors WHERE userId = :userId AND category = :category ORDER BY name")
    fun getVendorsByCategory(userId: String, category: VendorCategory): Flow<List<Vendor>>
    
    @Query("SELECT * FROM vendors WHERE userId = :userId AND status = :status ORDER BY name")
    fun getVendorsByStatus(userId: String, status: VendorStatus): Flow<List<Vendor>>
    
    @Query("SELECT * FROM vendors WHERE userId = :userId AND status = :bookedStatus ORDER BY category")
    fun getBookedVendors(userId: String, bookedStatus: VendorStatus = VendorStatus.BOOKED): Flow<List<Vendor>>
    
    @Query("SELECT * FROM vendors WHERE id = :id")
    suspend fun getVendorById(id: Long): Vendor?
    
    @Query("SELECT COUNT(*) FROM vendors WHERE userId = :userId")
    fun getVendorCount(userId: String): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM vendors WHERE userId = :userId AND status = :status")
    fun getVendorCountByStatus(userId: String, status: VendorStatus): Flow<Int>
    
    @Query("SELECT SUM(agreedPrice) FROM vendors WHERE userId = :userId AND status IN ('BOOKED', 'DEPOSIT_PAID', 'COMPLETED')")
    fun getTotalBookedAmount(userId: String): Flow<Double?>
    
    @Query("SELECT SUM(depositAmount) FROM vendors WHERE userId = :userId AND depositPaid = 1")
    fun getTotalDepositsPaid(userId: String): Flow<Double?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendor(vendor: Vendor): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendors(vendors: List<Vendor>)
    
    @Update
    suspend fun updateVendor(vendor: Vendor)
    
    @Delete
    suspend fun deleteVendor(vendor: Vendor)
    
    @Query("DELETE FROM vendors WHERE id = :id")
    suspend fun deleteVendorById(id: Long)
    
    @Query("DELETE FROM vendors WHERE userId = :userId")
    suspend fun deleteAllVendors(userId: String)
}
