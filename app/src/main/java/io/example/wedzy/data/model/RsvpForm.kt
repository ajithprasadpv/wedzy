package io.example.wedzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rsvp_forms")
data class RsvpForm(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "",
    val eventId: Long = 0,
    val shareableLink: String = "",
    val qrCodeBase64: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null,
    val responseCount: Int = 0
)

data class RsvpResponse(
    val guestToken: String,
    val guestName: String,
    val email: String,
    val phone: String,
    val rsvpStatus: String,
    val mealPreference: String,
    val dietaryRestrictions: List<String>,
    val plusOne: Boolean,
    val plusOneName: String,
    val songRequest: String,
    val message: String,
    val submittedAt: Long = System.currentTimeMillis()
)

data class MealOption(
    val id: String,
    val name: String,
    val description: String
)

val defaultMealOptions = listOf(
    MealOption("chicken", "Chicken", "Grilled chicken breast with seasonal vegetables"),
    MealOption("beef", "Beef", "Prime beef tenderloin with roasted potatoes"),
    MealOption("fish", "Fish", "Pan-seared salmon with lemon butter sauce"),
    MealOption("vegetarian", "Vegetarian", "Vegetable medley with quinoa"),
    MealOption("vegan", "Vegan", "Plant-based protein with seasonal vegetables")
)

val defaultDietaryRestrictions = listOf(
    "None",
    "Vegetarian",
    "Vegan",
    "Gluten-Free",
    "Dairy-Free",
    "Nut Allergy",
    "Shellfish Allergy",
    "Kosher",
    "Halal"
)
