package io.example.wedzy.data.model

data class BudgetRecommendation(
    val category: BudgetCategory,
    val minPercentage: Double,
    val maxPercentage: Double,
    val averagePercentage: Double,
    val description: String
)

object IndustryBudgetStandards {
    val recommendations: Map<BudgetCategory, BudgetRecommendation> = mapOf(
        BudgetCategory.VENUE to BudgetRecommendation(
            category = BudgetCategory.VENUE,
            minPercentage = 40.0,
            maxPercentage = 50.0,
            averagePercentage = 45.0,
            description = "Ceremony and reception venue rental"
        ),
        BudgetCategory.CATERING to BudgetRecommendation(
            category = BudgetCategory.CATERING,
            minPercentage = 25.0,
            maxPercentage = 30.0,
            averagePercentage = 28.0,
            description = "Food, beverages, and service"
        ),
        BudgetCategory.PHOTOGRAPHY to BudgetRecommendation(
            category = BudgetCategory.PHOTOGRAPHY,
            minPercentage = 10.0,
            maxPercentage = 15.0,
            averagePercentage = 12.0,
            description = "Photography and videography"
        ),
        BudgetCategory.MUSIC_DJ to BudgetRecommendation(
            category = BudgetCategory.MUSIC_DJ,
            minPercentage = 8.0,
            maxPercentage = 10.0,
            averagePercentage = 9.0,
            description = "DJ, band, or live music"
        ),
        BudgetCategory.FLOWERS to BudgetRecommendation(
            category = BudgetCategory.FLOWERS,
            minPercentage = 8.0,
            maxPercentage = 10.0,
            averagePercentage = 9.0,
            description = "Flowers, centerpieces, and decorations"
        ),
        BudgetCategory.ATTIRE_BRIDE to BudgetRecommendation(
            category = BudgetCategory.ATTIRE_BRIDE,
            minPercentage = 5.0,
            maxPercentage = 8.0,
            averagePercentage = 6.0,
            description = "Wedding dress and accessories"
        ),
        BudgetCategory.INVITATIONS to BudgetRecommendation(
            category = BudgetCategory.INVITATIONS,
            minPercentage = 2.0,
            maxPercentage = 3.0,
            averagePercentage = 2.5,
            description = "Invitations and stationery"
        ),
        BudgetCategory.TRANSPORTATION to BudgetRecommendation(
            category = BudgetCategory.TRANSPORTATION,
            minPercentage = 2.0,
            maxPercentage = 3.0,
            averagePercentage = 2.5,
            description = "Transportation for couple and guests"
        ),
        BudgetCategory.OTHER to BudgetRecommendation(
            category = BudgetCategory.OTHER,
            minPercentage = 5.0,
            maxPercentage = 10.0,
            averagePercentage = 7.0,
            description = "Miscellaneous expenses and contingency"
        )
    )
    
    fun getRecommendation(category: BudgetCategory): BudgetRecommendation? {
        return recommendations[category]
    }
    
    fun getAllRecommendations(): List<BudgetRecommendation> {
        return recommendations.values.toList()
    }
}
