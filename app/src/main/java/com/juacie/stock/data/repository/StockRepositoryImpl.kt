package com.juacie.stock.data.repository

import com.juacie.stock.data.remote.TwseApi
import com.juacie.stock.domain.model.Stock
import com.juacie.stock.domain.repository.StockRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StockRepositoryImpl(private val api: TwseApi) : StockRepository {
    override fun getStocks(): Flow<List<Stock>> = flow {
        while (true) {
            try {
                val dayAll = api.getStockDayAll()
                val bwibbuAll = api.getBwibbuAll().associateBy { it.code }
                val dayAvgAll = api.getStockDayAvgAll().associateBy { it.code }

                val stocks = dayAll.map { day ->
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
                emit(stocks)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            delay(60000) // Refresh every 1 minute
        }
    }
}
