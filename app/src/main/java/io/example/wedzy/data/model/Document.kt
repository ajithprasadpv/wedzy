package io.example.wedzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DocumentCategory {
    CONTRACT, INVOICE, RECEIPT, PHOTO, INSPIRATION,
    CHECKLIST, NOTES, FLOOR_PLAN, MENU, INVITATION,
    GUEST_LIST, SEATING_CHART, TIMELINE, OTHER
}

@Entity(tableName = "documents")
data class Document(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "",
    val name: String,
    val category: DocumentCategory = DocumentCategory.OTHER,
    val filePath: String,
    val fileType: String, // pdf, jpg, png, etc.
    val fileSize: Long = 0, // in bytes
    val thumbnailPath: String? = null,
    val vendorId: Long? = null, // Link to vendor if applicable
    val notes: String = "",
    val tags: String = "", // Comma-separated tags
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
