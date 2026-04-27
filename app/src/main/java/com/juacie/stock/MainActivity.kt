package com.juacie.stock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.juacie.stock.presentation.stock_list.StockListScreen
import com.juacie.stock.presentation.stock_list.StockListViewModel
import com.juacie.stock.presentation.stock_list.StockListViewModelFactory
import com.juacie.stock.ui.theme.StockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * 遵循業界標準的架構：
         * MainActivity 僅負責 UI 初始化，所有相依物件 (UseCase, Repository) 
         * 都透過 StockApp 的 container 取得。
         */
        val container = (application as StockApp).container
        val factory = StockListViewModelFactory(container.getStocksUseCase)
        val viewModel = ViewModelProvider(this, factory)[StockListViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            StockTheme {
                StockListScreen(viewModel = viewModel)
            }
        }
    }
}
