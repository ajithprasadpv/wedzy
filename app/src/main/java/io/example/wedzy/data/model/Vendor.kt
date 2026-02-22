package io.example.wedzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class VendorCategory {
    VENUE,
    CATERER,
    PHOTOGRAPHER,
    VIDEOGRAPHER,
    FLORIST,
    DECORATOR,
    DJ_MUSIC,
    CAKE_BAKER,
    MAKEUP_ARTIST,
    HAIR_STYLIST,
    WEDDING_PLANNER,
    OFFICIANT,
    TRANSPORTATION,
    JEWELER,
    DRESS_DESIGNER,
    SUIT_TAILOR,
    INVITATION_DESIGNER,
    RENTALS,
    OTHER
}

enum class VendorStatus {
    RESEARCHING,
    CONTACTED,
    MEETING_SCHEDULED,
    PROPOSAL_RECEIVED,
    BOOKED,
    DEPOSIT_PAID,
    COMPLETED,
    CANCELLED
}

@Entity(tableName = "vendors")
data class Vendor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "",
    val name: String,
    val category: VendorCategory,
    val contactPerson: String = "",
    val email: String = "",
    val phone: String = "",
    val website: String = "",
    val address: String = "",
    val status: VendorStatus = VendorStatus.RESEARCHING,
    val quotedPrice: Double = 0.0,
    val agreedPrice: Double = 0.0,
    val depositAmount: Double = 0.0,
    val depositPaid: Boolean = false,
    val contractUri: String? = null,
    val rating: Int = 0,
    val notes: String = "",
    val meetingDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
