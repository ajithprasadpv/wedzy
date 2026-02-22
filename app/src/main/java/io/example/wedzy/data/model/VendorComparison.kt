package io.example.wedzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vendor_comparisons")
data class VendorComparison(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "",
    val comparisonName: String,
    val vendorIds: String, // Comma-separated vendor IDs
    val selectedVendorId: Long? = null,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getVendorIdsList(): List<Long> {
        return vendorIds.split(",").mapNotNull { it.toLongOrNull() }
    }
    
    companion object {
        fun fromVendorIds(name: String, ids: List<Long>): VendorComparison {
            return VendorComparison(
                comparisonName = name,
                vendorIds = ids.joinToString(",")
            )
        }
    }
}

data class VendorComparisonResult(
    val vendor: Vendor,
    val score: Double,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val priceRank: Int,
    val overallRank: Int
)

data class ComparisonCriteria(
    val name: String,
    val weight: Double,
    val getValue: (Vendor) -> Double
)

val defaultComparisonCriteria = listOf(
    ComparisonCriteria("Price", 0.4) { vendor ->
        val maxPrice = 100000.0
        val price = vendor.quotedPrice ?: vendor.agreedPrice ?: 0.0
        if (price == 0.0) 0.0 else (maxPrice - price) / maxPrice * 100
    },
    ComparisonCriteria("Status", 0.2) { vendor ->
        when (vendor.status) {
            VendorStatus.BOOKED -> 100.0
            VendorStatus.PROPOSAL_RECEIVED -> 80.0
            VendorStatus.MEETING_SCHEDULED -> 70.0
            VendorStatus.CONTACTED -> 60.0
            VendorStatus.RESEARCHING -> 40.0
            VendorStatus.DEPOSIT_PAID -> 90.0
            VendorStatus.COMPLETED -> 100.0
            VendorStatus.CANCELLED -> 0.0
        }
    },
    ComparisonCriteria("Responsiveness", 0.2) { vendor ->
        if (vendor.contactPerson.isNotEmpty() && vendor.email.isNotEmpty()) 80.0 else 50.0
    },
    ComparisonCriteria("Completeness", 0.2) { vendor ->
        var score = 0.0
        if (vendor.name.isNotEmpty()) score += 20
        if (vendor.contactPerson.isNotEmpty()) score += 20
        if (vendor.phone.isNotEmpty()) score += 20
        if (vendor.email.isNotEmpty()) score += 20
        if (vendor.notes.isNotEmpty()) score += 20
        score
    }
)
