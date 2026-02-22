package io.example.wedzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class InspirationCategory {
    VENUE, DECOR, FLOWERS, DRESS, SUIT, CAKE,
    FOOD, DRINKS, PHOTOGRAPHY, INVITATION,
    HAIR_MAKEUP, FAVORS, ENTERTAINMENT, OTHER
}

@Entity(tableName = "inspirations")
data class Inspiration(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "",
    val title: String = "",
    val category: InspirationCategory = InspirationCategory.OTHER,
    val imageUrl: String = "",
    val localImagePath: String? = null,
    val sourceUrl: String = "",
    val notes: String = "",
    val tags: String = "", // Comma-separated tags
    val isFavorite: Boolean = false,
    val isFromMarketplace: Boolean = false,
    val vendorId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
