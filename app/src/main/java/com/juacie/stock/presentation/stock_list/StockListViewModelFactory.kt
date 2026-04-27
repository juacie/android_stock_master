package com.juacie.stock.presentation.stock_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.juacie.stock.domain.usecase.GetStocksUseCase

class StockListViewModelFactory(
    private val getStocksUseCase: GetStocksUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StockListViewModel(getStocksUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
