package com.juacie.stock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.juacie.stock.data.remote.TwseApi
import com.juacie.stock.data.repository.StockRepositoryImpl
import com.juacie.stock.domain.usecase.GetStocksUseCase
import com.juacie.stock.presentation.stock_list.StockListScreen
import com.juacie.stock.presentation.stock_list.StockListViewModel
import com.juacie.stock.presentation.stock_list.StockListViewModelFactory
import com.juacie.stock.ui.theme.StockTheme
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Manual DI with Retrofit
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val api = Retrofit.Builder()
            .baseUrl(TwseApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(TwseApi::class.java)

        val repository = StockRepositoryImpl(api)
        val getStocksUseCase = GetStocksUseCase(repository)
        val factory = StockListViewModelFactory(getStocksUseCase)
        val viewModel = ViewModelProvider(this, factory)[StockListViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            StockTheme {
                StockListScreen(viewModel = viewModel)
            }
        }
    }
}
