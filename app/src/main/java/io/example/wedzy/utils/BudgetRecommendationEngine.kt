package io.example.wedzy.utils

import io.example.wedzy.data.model.BudgetCategory
import io.example.wedzy.data.model.BudgetItem
import io.example.wedzy.data.model.IndustryBudgetStandards

object BudgetRecommendationEngine {
    
    data class CategoryAllocation(
        val category: BudgetCategory,
        val recommendedAmount: Double,
        val minAmount: Double,
        val maxAmount: Double,
        val percentage: Double,
        val minPercentage: Double,
        val maxPercentage: Double,
        val description: String
    )
    
    fun calculateRecommendedAllocations(totalBudget: Double): List<CategoryAllocation> {
        return IndustryBudgetStandards.getAllRecommendations().map { recommendation ->
            CategoryAllocation(
                category = recommendation.category,
                recommendedAmount = totalBudget * (recommendation.averagePercentage / 100),
                minAmount = totalBudget * (recommendation.minPercentage / 100),
                maxAmount = totalBudget * (recommendation.maxPercentage / 100),
                percentage = recommendation.averagePercentage,
                minPercentage = recommendation.minPercentage,
                maxPercentage = recommendation.maxPercentage,
                description = recommendation.description
            )
        }.sortedByDescending { it.recommendedAmount }
    }
    
    fun generateRecommendedBudgetItems(totalBudget: Double): List<BudgetItem> {
        return calculateRecommendedAllocations(totalBudget).map { allocation ->
            BudgetItem(
                name = getCategoryDefaultName(allocation.category),
                category = allocation.category,
                estimatedCost = allocation.recommendedAmount,
                actualCost = 0.0,
                notes = allocation.description
            )
        }
    }
    
    fun analyzeCurrentBudget(
        items: List<BudgetItem>,
        totalBudget: Double
    ): BudgetAnalysis {
        val categoryTotals = items.groupBy { it.category }
            .mapValues { (_, items) -> items.sumOf { it.estimatedCost } }
        
        val recommendations = calculateRecommendedAllocations(totalBudget)
        
        val categoryAnalysis = recommendations.map { recommendation ->
            val currentAmount = categoryTotals[recommendation.category] ?: 0.0
            val currentPercentage = if (totalBudget > 0) (currentAmount / totalBudget) * 100 else 0.0
            
            CategoryBudgetAnalysis(
                category = recommendation.category,
                currentAmount = currentAmount,
                currentPercentage = currentPercentage,
                recommendedAmount = recommendation.recommendedAmount,
                recommendedPercentage = recommendation.percentage,
                difference = currentAmount - recommendation.recommendedAmount,
                status = when {
                    currentAmount == 0.0 -> AllocationStatus.NOT_SET
                    currentPercentage < recommendation.minPercentage -> AllocationStatus.UNDER_ALLOCATED
                    currentPercentage > recommendation.maxPercentage -> AllocationStatus.OVER_ALLOCATED
                    else -> AllocationStatus.OPTIMAL
                }
            )
        }
        
        val totalAllocated = categoryTotals.values.sum()
        val unallocated = totalBudget - totalAllocated
        
        return BudgetAnalysis(
            totalBudget = totalBudget,
            totalAllocated = totalAllocated,
            unallocated = unallocated,
            categoryAnalysis = categoryAnalysis,
            overallStatus = when {
                totalAllocated > totalBudget -> AllocationStatus.OVER_ALLOCATED
                totalAllocated < totalBudget * 0.8 -> AllocationStatus.UNDER_ALLOCATED
                else -> AllocationStatus.OPTIMAL
            }
        )
    }
    
    private fun getCategoryDefaultName(category: BudgetCategory): String {
        return when (category) {
            BudgetCategory.VENUE -> "Venue Rental"
            BudgetCategory.CATERING -> "Catering Services"
            BudgetCategory.PHOTOGRAPHY -> "Photography & Videography"
            BudgetCategory.VIDEOGRAPHY -> "Videography"
            BudgetCategory.DECORATION -> "Decorations"
            BudgetCategory.FLOWERS -> "Flowers & Floral Arrangements"
            BudgetCategory.ATTIRE_BRIDE -> "Bride's Attire"
            BudgetCategory.ATTIRE_GROOM -> "Groom's Attire"
            BudgetCategory.JEWELRY -> "Jewelry & Accessories"
            BudgetCategory.MUSIC_DJ -> "Music & Entertainment"
            BudgetCategory.INVITATIONS -> "Invitations & Stationery"
            BudgetCategory.TRANSPORTATION -> "Transportation"
            BudgetCategory.HONEYMOON -> "Honeymoon"
            BudgetCategory.GIFTS -> "Gifts"
            BudgetCategory.MAKEUP_HAIR -> "Makeup & Hair"
            BudgetCategory.CAKE -> "Wedding Cake"
            BudgetCategory.OFFICIANT -> "Officiant"
            BudgetCategory.RENTALS -> "Rentals"
            BudgetCategory.OTHER -> "Miscellaneous"
        }
    }
}

data class BudgetAnalysis(
    val totalBudget: Double,
    val totalAllocated: Double,
    val unallocated: Double,
    val categoryAnalysis: List<CategoryBudgetAnalysis>,
    val overallStatus: AllocationStatus
)

data class CategoryBudgetAnalysis(
    val category: BudgetCategory,
    val currentAmount: Double,
    val currentPercentage: Double,
    val recommendedAmount: Double,
    val recommendedPercentage: Double,
    val difference: Double,
    val status: AllocationStatus
)

enum class AllocationStatus {
    NOT_SET,
    UNDER_ALLOCATED,
    OPTIMAL,
    OVER_ALLOCATED
}
