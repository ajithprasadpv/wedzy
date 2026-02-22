package io.example.wedzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wedding_profile")
data class WeddingProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "",
    val brideName: String = "",
    val groomName: String = "",
    val weddingDate: Long = 0L,
    val venueName: String = "",
    val venueAddress: String = "",
    val totalBudget: Double = 0.0,
    val currency: String = "USD",
    val profileImageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
