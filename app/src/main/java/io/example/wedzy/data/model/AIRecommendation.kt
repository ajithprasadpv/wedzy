package io.example.wedzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RecommendationType {
    VENDOR, TASK, BUDGET_TIP, TIMELINE, INSPIRATION,
    TEMPLATE, SEATING, GUEST_MANAGEMENT, GENERAL
}

enum class RecommendationPriority {
    LOW, MEDIUM, HIGH, URGENT
}

@Entity(tableName = "ai_recommendations")
data class AIRecommendation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: RecommendationType,
    val title: String,
    val description: String,
    val reason: String = "", // Why this is recommended
    val actionLabel: String = "", // e.g., "View Vendor", "Add Task"
    val actionData: String = "", // JSON data for the action
    val priority: RecommendationPriority = RecommendationPriority.MEDIUM,
    val relatedEntityId: Long? = null,
    val relatedEntityType: String = "", // vendor, task, etc.
    val isRead: Boolean = false,
    val isDismissed: Boolean = false,
    val isActedUpon: Boolean = false,
    val confidence: Float = 0f, // AI confidence score 0-1
    val expiresAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_preferences_ai")
data class UserAIPreferences(
    @PrimaryKey
    val id: Long = 1,
    val enableRecommendations: Boolean = true,
    val preferredStyle: String = "", // Wedding style preference
    val budgetSensitivity: String = "medium", // low, medium, high
    val preferLocalVendors: Boolean = true,
    val preferredCategories: String = "", // Comma-separated preferred categories
    val excludedCategories: String = "", // Categories to exclude
    val lastAnalyzedAt: Long? = null
)
