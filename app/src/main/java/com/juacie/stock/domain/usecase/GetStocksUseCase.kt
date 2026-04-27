package com.juacie.stock.domain.usecase

import com.juacie.stock.domain.model.Stock
import com.juacie.stock.domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow

class GetStocksUseCase(
    private val repository: StockRepository
) {
    operator fun invoke(): Flow<List<Stock>> {
        return repository.getStocks()
    }
}
