package com.juacie.stock

import android.app.Application
import com.juacie.stock.di.AppContainer
import com.juacie.stock.di.AppDataContainer

/**
 * 自定義 Application 類別
 * 負責在整個 App 生命週期中保存全域實體 (例如 AppContainer)。
 */
class StockApp : Application() {
    
    // 全域的相依注入容器
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // 初始化容器
        container = AppDataContainer()
    }
}
