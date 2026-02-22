package io.example.wedzy.data.model

enum class Currency(
    val code: String,
    val symbol: String,
    val displayName: String
) {
    USD("USD", "$", "US Dollar"),
    INR("INR", "₹", "Indian Rupee"),
    GBP("GBP", "£", "British Pound"),
    EUR("EUR", "€", "Euro"),
    AUD("AUD", "A$", "Australian Dollar"),
    CAD("CAD", "C$", "Canadian Dollar"),
    JPY("JPY", "¥", "Japanese Yen"),
    CNY("CNY", "¥", "Chinese Yuan"),
    AED("AED", "د.إ", "UAE Dirham"),
    SGD("SGD", "S$", "Singapore Dollar");
    
    companion object {
        fun fromCode(code: String): Currency {
            return entries.find { it.code == code } ?: USD
        }
    }
}
