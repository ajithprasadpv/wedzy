package io.example.wedzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_dependencies")
data class TaskDependency(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "",
    val taskId: Long,
    val dependsOnTaskId: Long,
    val dependencyType: DependencyType = DependencyType.BLOCKS,
    val delayDays: Int = 0
)

enum class DependencyType {
    BLOCKS,      // Task cannot start until dependency is complete
    SUGGESTS     // Task is suggested to start after dependency
}

object TaskDependencyTemplates {
    val dependencies = listOf(
        // Venue must be booked before most other vendors
        TaskDependencyTemplate("Book venue", "Book caterer", DependencyType.BLOCKS, 7),
        TaskDependencyTemplate("Book venue", "Book florist", DependencyType.BLOCKS, 7),
        TaskDependencyTemplate("Book venue", "Plan rehearsal dinner", DependencyType.BLOCKS, 0),
        
        // Date must be set before sending save-the-dates
        TaskDependencyTemplate("Set wedding budget", "Book venue", DependencyType.SUGGESTS, 0),
        TaskDependencyTemplate("Create guest list draft", "Send save-the-dates", DependencyType.BLOCKS, 0),
        
        // Photography before videography
        TaskDependencyTemplate("Book photographer", "Book videographer", DependencyType.SUGGESTS, 0),
        
        // Invitations sequence
        TaskDependencyTemplate("Order wedding invitations", "Mail wedding invitations", DependencyType.BLOCKS, 30),
        TaskDependencyTemplate("Send save-the-dates", "Order wedding invitations", DependencyType.SUGGESTS, 90),
        
        // Attire sequence
        TaskDependencyTemplate("Shop for wedding dress", "Schedule dress fittings", DependencyType.BLOCKS, 60),
        TaskDependencyTemplate("Schedule dress fittings", "Final dress fitting", DependencyType.BLOCKS, 30),
        
        // Catering sequence
        TaskDependencyTemplate("Book caterer", "Order wedding cake", DependencyType.SUGGESTS, 0),
        TaskDependencyTemplate("Confirm final guest count", "Finalize reception details", DependencyType.BLOCKS, 0),
        
        // Final preparations
        TaskDependencyTemplate("Finalize seating chart", "Order wedding programs", DependencyType.SUGGESTS, 0),
        TaskDependencyTemplate("Confirm all vendor details", "Prepare vendor payments and tips", DependencyType.BLOCKS, 7),
        TaskDependencyTemplate("Apply for marriage license", "Rehearsal and rehearsal dinner", DependencyType.BLOCKS, 0)
    )
}

data class TaskDependencyTemplate(
    val taskTitle: String,
    val dependsOnTitle: String,
    val type: DependencyType,
    val delayDays: Int
)
