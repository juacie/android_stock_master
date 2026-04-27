package com.juacie.stock.di

import com.juacie.stock.data.remote.TwseApi
import com.juacie.stock.data.repository.StockRepositoryImpl
import com.juacie.stock.domain.repository.StockRepository
import com.juacie.stock.domain.usecase.GetStocksUseCase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 相依注入容器 (AppContainer)
 * 負責集中管理與建立整個 App 需要的 Singleton 實體。
 * 這符合中大型專案的架構標準，確保 MainActivity 不再負擔建立邏輯。
 */
interface AppContainer {
    val stockRepository: StockRepository
    val getStocksUseCase: GetStocksUseCase
}

/**
 * 實際的相依注入容器實作
 */
class AppDataContainer : AppContainer {
    
    // 設定 OkHttpClient (包含 Timeout 邏輯)
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    // 建立 Retrofit API 介面
    private val api: TwseApi = Retrofit.Builder()
        .baseUrl(TwseApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
        .create(TwseApi::class.java)

    // 使用 lazy 確保只在需要時才建立實體
    override val stockRepository: StockRepository by lazy {
        StockRepositoryImpl(api)
    }

    override val getStocksUseCase: GetStocksUseCase by lazy {
        GetStocksUseCase(stockRepository)
    }
}
