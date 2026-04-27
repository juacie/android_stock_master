package com.juacie.stock.domain.repository

import com.juacie.stock.domain.model.Stock
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    fun getStocks(): Flow<List<Stock>>
}
