package io.example.wedzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vendor_reviews")
data class VendorReview(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vendorId: Long,
    val rating: Float, // 1-5 stars
    val title: String = "",
    val review: String = "",
    val reviewerName: String = "",
    val weddingDate: Long? = null,
    val photos: String = "", // Comma-separated photo URLs
    val responseFromVendor: String = "",
    val isVerified: Boolean = false,
    val helpfulCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "marketplace_vendors")
data class MarketplaceVendor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val description: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val serviceArea: String = "", // Cities/regions served
    val priceRange: String = "", // e.g., "$$$", "$$$$"
    val minPrice: Double = 0.0,
    val maxPrice: Double = 0.0,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val photoUrls: String = "", // Comma-separated
    val websiteUrl: String = "",
    val email: String = "",
    val phone: String = "",
    val isVerified: Boolean = false,
    val isPremium: Boolean = false,
    val isFavorited: Boolean = false,
    val responseTime: String = "", // e.g., "Within 24 hours"
    val availability: String = "", // Booked dates info
    val createdAt: Long = System.currentTimeMillis()
)
