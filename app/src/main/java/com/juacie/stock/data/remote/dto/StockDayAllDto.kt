package com.juacie.stock.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StockDayAllDto(
    @SerializedName("Code") val code: String,
    @SerializedName("Name") val name: String,
    @SerializedName("OpeningPrice") val openingPrice: String,
    @SerializedName("HighestPrice") val highestPrice: String,
    @SerializedName("LowestPrice") val lowestPrice: String,
    @SerializedName("ClosingPrice") val closingPrice: String,
    @SerializedName("Change") val change: String,
    @SerializedName("TradeVolume") val tradeVolume: String
)
