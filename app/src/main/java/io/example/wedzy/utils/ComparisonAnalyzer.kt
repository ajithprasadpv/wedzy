package io.example.wedzy.utils

import io.example.wedzy.data.model.Vendor
import io.example.wedzy.data.model.VendorComparisonResult
import io.example.wedzy.data.model.defaultComparisonCriteria

object ComparisonAnalyzer {
    
    fun analyzeVendors(vendors: List<Vendor>): List<VendorComparisonResult> {
        if (vendors.isEmpty()) return emptyList()
        
        val results = vendors.map { vendor ->
            val score = calculateOverallScore(vendor)
            val strengths = identifyStrengths(vendor, vendors)
            val weaknesses = identifyWeaknesses(vendor, vendors)
            
            VendorComparisonResult(
                vendor = vendor,
                score = score,
                strengths = strengths,
                weaknesses = weaknesses,
                priceRank = 0,
                overallRank = 0
            )
        }
        
        val sortedByScore = results.sortedByDescending { it.score }
        val sortedByPrice = results.sortedBy { it.vendor.quotedPrice ?: it.vendor.agreedPrice ?: Double.MAX_VALUE }
        
        return results.map { result ->
            result.copy(
                overallRank = sortedByScore.indexOf(result) + 1,
                priceRank = sortedByPrice.indexOf(result) + 1
            )
        }
    }
    
    fun calculateOverallScore(vendor: Vendor): Double {
        return defaultComparisonCriteria.sumOf { criteria ->
            criteria.getValue(vendor) * criteria.weight
        }
    }
    
    fun getBestValue(vendors: List<Vendor>): Vendor? {
        return analyzeVendors(vendors).maxByOrNull { it.score }?.vendor
    }
    
    fun getLowestPrice(vendors: List<Vendor>): Vendor? {
        return vendors.minByOrNull { it.quotedPrice ?: it.agreedPrice ?: Double.MAX_VALUE }
    }
    
    private fun identifyStrengths(vendor: Vendor, allVendors: List<Vendor>): List<String> {
        val strengths = mutableListOf<String>()
        
        val price = vendor.quotedPrice ?: vendor.agreedPrice ?: 0.0
        val avgPrice = allVendors.mapNotNull { it.quotedPrice ?: it.agreedPrice }.average()
        
        if (price > 0 && price < avgPrice * 0.9) {
            strengths.add("Competitive pricing (${String.format("%.0f", ((avgPrice - price) / avgPrice * 100))}% below average)")
        }
        
        if (vendor.status.name in listOf("BOOKED", "DEPOSIT_PAID", "COMPLETED")) {
            strengths.add("Confirmed availability")
        }
        
        if (vendor.contactPerson.isNotEmpty() && vendor.email.isNotEmpty() && vendor.phone.isNotEmpty()) {
            strengths.add("Complete contact information")
        }
        
        if (vendor.notes.length > 100) {
            strengths.add("Detailed notes available")
        }
        
        return strengths
    }
    
    private fun identifyWeaknesses(vendor: Vendor, allVendors: List<Vendor>): List<String> {
        val weaknesses = mutableListOf<String>()
        
        val price = vendor.quotedPrice ?: vendor.agreedPrice ?: 0.0
        val avgPrice = allVendors.mapNotNull { it.quotedPrice ?: it.agreedPrice }.average()
        
        if (price > avgPrice * 1.1) {
            weaknesses.add("Above average price (${String.format("%.0f", ((price - avgPrice) / avgPrice * 100))}% higher)")
        }
        
        if (vendor.status.name in listOf("RESEARCHING", "CANCELLED")) {
            weaknesses.add("Uncertain availability")
        }
        
        if (vendor.email.isEmpty()) {
            weaknesses.add("Missing email contact")
        }
        
        if (vendor.phone.isEmpty()) {
            weaknesses.add("Missing phone contact")
        }
        
        if (vendor.quotedPrice == null && vendor.agreedPrice == null) {
            weaknesses.add("No pricing information")
        }
        
        return weaknesses
    }
    
    fun generateComparisonSummary(vendors: List<Vendor>): String {
        if (vendors.isEmpty()) return "No vendors to compare"
        
        val results = analyzeVendors(vendors)
        val bestValue = results.maxByOrNull { it.score }
        val lowestPrice = results.minByOrNull { it.vendor.quotedPrice ?: it.vendor.agreedPrice ?: Double.MAX_VALUE }
        
        return buildString {
            appendLine("Comparison Summary:")
            appendLine()
            appendLine("Best Overall Value: ${bestValue?.vendor?.name}")
            appendLine("Lowest Price: ${lowestPrice?.vendor?.name}")
            appendLine()
            appendLine("Rankings:")
            results.sortedBy { it.overallRank }.forEach { result ->
                appendLine("${result.overallRank}. ${result.vendor.name} - Score: ${String.format("%.1f", result.score)}/100")
            }
        }
    }
}
