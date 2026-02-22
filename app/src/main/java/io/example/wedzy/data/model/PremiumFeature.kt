package io.example.wedzy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SubscriptionTier {
    FREE, BASIC, PREMIUM, ULTIMATE
}

enum class FeatureType {
    UNLIMITED_GUESTS, UNLIMITED_VENDORS, UNLIMITED_DOCUMENTS,
    AI_RECOMMENDATIONS, VENDOR_MARKETPLACE, ADVANCED_ANALYTICS,
    COLLABORATION, SEATING_TOOL, PREMIUM_TEMPLATES,
    PRIORITY_SUPPORT, AD_FREE, EXPORT_REPORTS,
    CALENDAR_SYNC, CUSTOM_BRANDING, WEBSITE_BUILDER
}

@Entity(tableName = "user_subscription")
data class UserSubscription(
    @PrimaryKey
    val id: Long = 1,
    val tier: SubscriptionTier = SubscriptionTier.FREE,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val isActive: Boolean = false,
    val autoRenew: Boolean = false,
    val paymentMethod: String = "",
    val lastPaymentDate: Long? = null,
    val nextPaymentDate: Long? = null,
    val pricePerMonth: Double = 0.0,
    val currency: String = "USD"
)

data class PremiumFeature(
    val type: FeatureType,
    val name: String,
    val description: String,
    val requiredTier: SubscriptionTier,
    val iconName: String = ""
)

object PremiumFeatures {
    val allFeatures = listOf(
        PremiumFeature(
            FeatureType.UNLIMITED_GUESTS,
            "Unlimited Guests",
            "Add unlimited guests to your wedding list",
            SubscriptionTier.BASIC
        ),
        PremiumFeature(
            FeatureType.UNLIMITED_VENDORS,
            "Unlimited Vendors",
            "Track unlimited vendors and contracts",
            SubscriptionTier.BASIC
        ),
        PremiumFeature(
            FeatureType.UNLIMITED_DOCUMENTS,
            "Unlimited Documents",
            "Store unlimited contracts, photos, and documents",
            SubscriptionTier.BASIC
        ),
        PremiumFeature(
            FeatureType.AI_RECOMMENDATIONS,
            "AI Recommendations",
            "Get personalized AI-powered suggestions",
            SubscriptionTier.PREMIUM
        ),
        PremiumFeature(
            FeatureType.VENDOR_MARKETPLACE,
            "Vendor Marketplace",
            "Access our curated vendor marketplace",
            SubscriptionTier.PREMIUM
        ),
        PremiumFeature(
            FeatureType.ADVANCED_ANALYTICS,
            "Advanced Analytics",
            "Detailed budget reports and insights",
            SubscriptionTier.PREMIUM
        ),
        PremiumFeature(
            FeatureType.COLLABORATION,
            "Team Collaboration",
            "Invite family and planners to collaborate",
            SubscriptionTier.PREMIUM
        ),
        PremiumFeature(
            FeatureType.SEATING_TOOL,
            "Seating Arrangement Tool",
            "Visual drag-and-drop seating planner",
            SubscriptionTier.PREMIUM
        ),
        PremiumFeature(
            FeatureType.PREMIUM_TEMPLATES,
            "Premium Templates",
            "Access exclusive wedding templates",
            SubscriptionTier.PREMIUM
        ),
        PremiumFeature(
            FeatureType.PRIORITY_SUPPORT,
            "Priority Support",
            "Get faster response from our support team",
            SubscriptionTier.ULTIMATE
        ),
        PremiumFeature(
            FeatureType.CUSTOM_BRANDING,
            "Custom Branding",
            "Personalize the app with your wedding colors",
            SubscriptionTier.ULTIMATE
        ),
        PremiumFeature(
            FeatureType.WEBSITE_BUILDER,
            "Wedding Website",
            "Build your own wedding website",
            SubscriptionTier.ULTIMATE
        )
    )
}
