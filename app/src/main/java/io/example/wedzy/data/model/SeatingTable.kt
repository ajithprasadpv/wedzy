package io.example.wedzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TableShape {
    ROUND, RECTANGULAR, SQUARE, OVAL, CUSTOM
}

@Entity(tableName = "seating_tables")
data class SeatingTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "",
    val name: String, // e.g., "Table 1", "Head Table"
    val tableNumber: Int = 0,
    val shape: TableShape = TableShape.ROUND,
    val capacity: Int = 8,
    val positionX: Float = 0f, // Position on floor plan
    val positionY: Float = 0f,
    val rotation: Float = 0f, // Rotation angle
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "seat_assignments")
data class SeatAssignment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "",
    val tableId: Long,
    val guestId: Long,
    val seatNumber: Int = 0, // Position at the table
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
