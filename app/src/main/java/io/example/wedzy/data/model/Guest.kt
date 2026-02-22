package io.example.wedzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RsvpStatus {
    PENDING,
    INVITED,
    CONFIRMED,
    DECLINED,
    MAYBE
}

enum class GuestSide {
    BRIDE,
    GROOM,
    MUTUAL
}

enum class GuestRelation {
    FAMILY,
    FRIEND,
    COLLEAGUE,
    OTHER
}

@Entity(tableName = "guests")
data class Guest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "",
    val firstName: String,
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val side: GuestSide = GuestSide.MUTUAL,
    val relation: GuestRelation = GuestRelation.OTHER,
    val rsvpStatus: RsvpStatus = RsvpStatus.PENDING,
    val plusOneAllowed: Boolean = false,
    val plusOneName: String? = null,
    val plusOneConfirmed: Boolean = false,
    val dietaryRestrictions: String = "",
    val specialRequirements: String = "",
    val tableNumber: Int? = null,
    val giftReceived: Boolean = false,
    val giftDescription: String = "",
    val thankYouSent: Boolean = false,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val fullName: String
        get() = "$firstName $lastName".trim()
}
