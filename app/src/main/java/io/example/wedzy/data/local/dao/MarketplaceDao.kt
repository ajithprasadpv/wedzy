package io.example.wedzy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.example.wedzy.data.model.MarketplaceVendor
import io.example.wedzy.data.model.VendorReview
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketplaceDao {
    @Query("SELECT * FROM marketplace_vendors ORDER BY rating DESC")
    fun getAllMarketplaceVendors(): Flow<List<MarketplaceVendor>>
    
    @Query("SELECT * FROM marketplace_vendors WHERE id = :vendorId")
    suspend fun getMarketplaceVendorById(vendorId: Long): MarketplaceVendor?
    
    @Query("SELECT * FROM marketplace_vendors WHERE category = :category ORDER BY rating DESC")
    fun getMarketplaceVendorsByCategory(category: String): Flow<List<MarketplaceVendor>>
    
    @Query("SELECT * FROM marketplace_vendors WHERE isFavorited = 1 ORDER BY name ASC")
    fun getFavoritedVendors(): Flow<List<MarketplaceVendor>>
    
    @Query("SELECT * FROM marketplace_vendors WHERE location LIKE '%' || :location || '%' ORDER BY rating DESC")
    fun getVendorsByLocation(location: String): Flow<List<MarketplaceVendor>>
    
    @Query("SELECT * FROM marketplace_vendors WHERE minPrice <= :maxBudget ORDER BY rating DESC")
    fun getVendorsWithinBudget(maxBudget: Double): Flow<List<MarketplaceVendor>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketplaceVendor(vendor: MarketplaceVendor): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketplaceVendors(vendors: List<MarketplaceVendor>)
    
    @Update
    suspend fun updateMarketplaceVendor(vendor: MarketplaceVendor)
    
    @Delete
    suspend fun deleteMarketplaceVendor(vendor: MarketplaceVendor)
    
    @Query("DELETE FROM marketplace_vendors")
    suspend fun deleteAllMarketplaceVendors()
    
    @Query("UPDATE marketplace_vendors SET isFavorited = :isFavorited WHERE id = :vendorId")
    suspend fun updateFavoriteStatus(vendorId: Long, isFavorited: Boolean)
    
    @Query("SELECT * FROM vendor_reviews WHERE vendorId = :vendorId ORDER BY createdAt DESC")
    fun getReviewsForVendor(vendorId: Long): Flow<List<VendorReview>>
    
    @Query("SELECT AVG(rating) FROM vendor_reviews WHERE vendorId = :vendorId")
    suspend fun getAverageRatingForVendor(vendorId: Long): Float?
    
    @Query("SELECT COUNT(*) FROM vendor_reviews WHERE vendorId = :vendorId")
    fun getReviewCountForVendor(vendorId: Long): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: VendorReview): Long
    
    @Update
    suspend fun updateReview(review: VendorReview)
    
    @Delete
    suspend fun deleteReview(review: VendorReview)
}
