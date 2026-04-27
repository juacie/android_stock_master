package com.juacie.stock.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StockDayAvgDto(
    @SerializedName("Code") val code: String,
    @SerializedName("Name") val name: String,
    @SerializedName("ClosingPrice") val closingPrice: String,
    @SerializedName("MonthlyAveragePrice") val monthlyAveragePrice: String
)
