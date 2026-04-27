package com.juacie.stock.data.repository

import com.juacie.stock.data.remote.TwseApi
import com.juacie.stock.domain.model.Stock
import com.juacie.stock.domain.repository.StockRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StockRepositoryImpl(private val api: TwseApi) : StockRepository {
    override fun getStocks(): Flow<List<Stock>> = flow {
        while (true) {
            // 使用 coroutineScope 來實現並行請求
            val stocks = coroutineScope {
                // 1. 同時啟動三個 API 請求
                val dayAllDeferred = async { api.getStockDayAll() }
                val bwibbuAllDeferred = async { api.getBwibbuAll() }
                val dayAvgAllDeferred = async { api.getStockDayAvgAll() }

                // 2. 等待結果並合併
                val dayAll = dayAllDeferred.await()
                val bwibbuAll = bwibbuAllDeferred.await().associateBy { it.code }
                val dayAvgAll = dayAvgAllDeferred.await().associateBy { it.code }

                // 3. 轉換為 Domain Model
                dayAll.map { day ->
                    val bwibbu = bwibbuAll[day.code]
                    val dayAvg = dayAvgAll[day.code]

                    val price = day.closingPrice.replace(",", "").toDoubleOrNull() ?: 0.0
                    val change = day.change.replace(",", "").toDoubleOrNull() ?: 0.0
                    val prevPrice = price - change
                    val changePercentage = if (prevPrice != 0.0) (change / prevPrice) * 100 else 0.0

                    Stock(
                        symbol = day.code,
                        name = day.name,
                        price = price,
                        change = change,
                        changePercentage = changePercentage,
                        open = day.openingPrice,
                        high = day.highestPrice,
                        low = day.lowestPrice,
                        volume = day.tradeVolume,
                        peRatio = bwibbu?.peRatio,
                        dividendYield = bwibbu?.dividendYield,
                        pbRatio = bwibbu?.pbRatio,
                        monthlyAveragePrice = dayAvg?.monthlyAveragePrice
                    )
                }
            }
            
            emit(stocks)
            delay(60000) // 每一分鐘更新一次
        }
    }
}
