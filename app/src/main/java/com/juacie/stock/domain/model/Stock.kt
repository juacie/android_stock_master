package com.juacie.stock.domain.model

data class Stock(
    val symbol: String,
    val name: String,
    val price: Double,
    val change: Double,
    val changePercentage: Double,
    val open: String? = null,
    val high: String? = null,
    val low: String? = null,
    val volume: String? = null,
    val peRatio: String? = null,
    val dividendYield: String? = null,
    val pbRatio: String? = null,
    val monthlyAveragePrice: String? = null,
    val isFavorite: Boolean = false
)
