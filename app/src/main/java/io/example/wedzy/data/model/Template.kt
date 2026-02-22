package io.example.wedzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TemplateType {
    TASK_CHECKLIST, BUDGET, TIMELINE, SEATING,
    GUEST_LIST, VENDOR_LIST, INVITATION, THANK_YOU,
    CEREMONY_PROGRAM, MENU, ITINERARY, OTHER
}

enum class WeddingStyle {
    CLASSIC, MODERN, RUSTIC, BOHEMIAN, BEACH,
    GARDEN, VINTAGE, GLAMOROUS, MINIMALIST,
    DESTINATION, CULTURAL, RELIGIOUS, OTHER
}

@Entity(tableName = "templates")
data class Template(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val type: TemplateType,
    val style: WeddingStyle = WeddingStyle.CLASSIC,
    val content: String = "", // JSON content
    val previewImageUrl: String = "",
    val isPremium: Boolean = false,
    val isDownloaded: Boolean = false,
    val downloadCount: Int = 0,
    val rating: Float = 0f,
    val authorName: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_templates")
data class UserTemplate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val templateId: Long,
    val customName: String = "",
    val customContent: String = "", // User's customized JSON content
    val isApplied: Boolean = false,
    val appliedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
