package com.juacie.stock.data.remote

import com.juacie.stock.data.remote.dto.StockBwibbuDto
import com.juacie.stock.data.remote.dto.StockDayAllDto
import com.juacie.stock.data.remote.dto.StockDayAvgDto
import retrofit2.http.GET

interface TwseApi {
    
    @GET("/v1/exchangeReport/BWIBBU_ALL")
    suspend fun getBwibbuAll(): List<StockBwibbuDto>

    @GET("/v1/exchangeReport/STOCK_DAY_ALL")
    suspend fun getStockDayAll(): List<StockDayAllDto>

    @GET("/v1/exchangeReport/STOCK_DAY_AVG_ALL")
    suspend fun getStockDayAvgAll(): List<StockDayAvgDto>

    companion object {
        const val BASE_URL = "https://openapi.twse.com.tw"
    }
}
