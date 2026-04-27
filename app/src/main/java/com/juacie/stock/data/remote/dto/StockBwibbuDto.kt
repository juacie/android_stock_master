package com.juacie.stock.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StockBwibbuDto(
    @SerializedName("Code") val code: String,
    @SerializedName("Name") val name: String,
    @SerializedName("PEratio") val peRatio: String,
    @SerializedName("DividendYield") val dividendYield: String,
    @SerializedName("PBratio") val pbRatio: String
)
