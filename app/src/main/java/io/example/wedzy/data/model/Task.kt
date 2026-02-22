package io.example.wedzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskPriority {
    LOW, MEDIUM, HIGH
}

enum class TaskStatus {
    PENDING, IN_PROGRESS, COMPLETED
}

enum class TaskCategory {
    VENUE,
    CATERING,
    PHOTOGRAPHY,
    DECORATION,
    ATTIRE,
    MUSIC,
    INVITATIONS,
    GUEST_MANAGEMENT,
    BUDGET,
    HONEYMOON,
    OTHER
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "",
    val title: String,
    val description: String = "",
    val dueDate: Long? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.PENDING,
    val category: TaskCategory = TaskCategory.OTHER,
    val assignedTo: String? = null,
    val notes: String = "",
    val isFromTemplate: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
