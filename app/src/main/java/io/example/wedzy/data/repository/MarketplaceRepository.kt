package io.example.wedzy.data.repository

import io.example.wedzy.data.local.dao.MarketplaceDao
import io.example.wedzy.data.model.MarketplaceVendor
import io.example.wedzy.data.model.VendorReview
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketplaceRepository @Inject constructor(
    private val marketplaceDao: MarketplaceDao
) {
    fun getAllMarketplaceVendors(): Flow<List<MarketplaceVendor>> = 
        marketplaceDao.getAllMarketplaceVendors()
    
    suspend fun getMarketplaceVendorById(vendorId: Long): MarketplaceVendor? = 
        marketplaceDao.getMarketplaceVendorById(vendorId)
    
    fun getMarketplaceVendorsByCategory(category: String): Flow<List<MarketplaceVendor>> = 
        marketplaceDao.getMarketplaceVendorsByCategory(category)
    
    fun getFavoritedVendors(): Flow<List<MarketplaceVendor>> = marketplaceDao.getFavoritedVendors()
    
    fun getVendorsByLocation(location: String): Flow<List<MarketplaceVendor>> = 
        marketplaceDao.getVendorsByLocation(location)
    
    fun getVendorsWithinBudget(maxBudget: Double): Flow<List<MarketplaceVendor>> = 
        marketplaceDao.getVendorsWithinBudget(maxBudget)
    
    suspend fun insertMarketplaceVendor(vendor: MarketplaceVendor): Long = 
        marketplaceDao.insertMarketplaceVendor(vendor)
    
    suspend fun insertMarketplaceVendors(vendors: List<MarketplaceVendor>) = 
        marketplaceDao.insertMarketplaceVendors(vendors)
    
    suspend fun updateMarketplaceVendor(vendor: MarketplaceVendor) = 
        marketplaceDao.updateMarketplaceVendor(vendor)
    
    suspend fun deleteMarketplaceVendor(vendor: MarketplaceVendor) = 
        marketplaceDao.deleteMarketplaceVendor(vendor)
    
    suspend fun deleteAllMarketplaceVendors() = 
        marketplaceDao.deleteAllMarketplaceVendors()
    
    suspend fun toggleFavorite(vendorId: Long, isFavorited: Boolean) = 
        marketplaceDao.updateFavoriteStatus(vendorId, isFavorited)
    
    fun getReviewsForVendor(vendorId: Long): Flow<List<VendorReview>> = 
        marketplaceDao.getReviewsForVendor(vendorId)
    
    suspend fun getAverageRatingForVendor(vendorId: Long): Float? = 
        marketplaceDao.getAverageRatingForVendor(vendorId)
    
    fun getReviewCountForVendor(vendorId: Long): Flow<Int> = 
        marketplaceDao.getReviewCountForVendor(vendorId)
    
    suspend fun insertReview(review: VendorReview): Long = marketplaceDao.insertReview(review)
    
    suspend fun updateReview(review: VendorReview) = marketplaceDao.updateReview(review)
    
    suspend fun deleteReview(review: VendorReview) = marketplaceDao.deleteReview(review)
}
