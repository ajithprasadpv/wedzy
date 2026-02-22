package io.example.wedzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BudgetCategory {
    VENUE,
    CATERING,
    PHOTOGRAPHY,
    VIDEOGRAPHY,
    DECORATION,
    FLOWERS,
    ATTIRE_BRIDE,
    ATTIRE_GROOM,
    JEWELRY,
    MUSIC_DJ,
    INVITATIONS,
    TRANSPORTATION,
    HONEYMOON,
    GIFTS,
    MAKEUP_HAIR,
    CAKE,
    OFFICIANT,
    RENTALS,
    OTHER
}

enum class PaymentStatus {
    NOT_PAID,
    DEPOSIT_PAID,
    PARTIALLY_PAID,
    FULLY_PAID
}

@Entity(tableName = "budget_items")
data class BudgetItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "",
    val name: String,
    val category: BudgetCategory,
    val estimatedCost: Double = 0.0,
    val actualCost: Double = 0.0,
    val paidAmount: Double = 0.0,
    val paymentStatus: PaymentStatus = PaymentStatus.NOT_PAID,
    val vendorId: Long? = null,
    val notes: String = "",
    val receiptUri: String? = null,
    val dueDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
