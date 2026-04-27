# StockMaster - 台灣股市即時觀察助手 📈

**StockMaster** 是一款基於 Clean Architecture 打造的 Android 應用程式，旨在提供使用者簡潔、即時的台灣上市股票觀察體驗。數據來源自「台灣證券交易所 OpenAPI」。

## 🚀 主要功能

- **即時行情**：自動從 TWSE 抓取最新股價、漲跌幅及成交量。
- **財務指標**：提供本益比 (PE)、殖利率、股價淨值比等關鍵數據。
- **詳盡細節**：點擊股票可展開 Bottom Sheet 查看開盤、最高、最低、月平均價等詳細資訊。
- **聰明搜尋**：支援透過「股票代號」或「公司名稱」快速篩選。
- **靈活排序**：可依代號、股價高低或漲跌百分比進行排序。
- **觀察名單**：點擊心形圖示可將心儀股票加入「我的最愛」分頁。
- **主題適配**：完整支援系統深色與淺色模式，保護您的眼睛。

## 🏗️ 系統架構

本專案嚴格遵循 **Clean Architecture** 與 **SOLID** 原則：

- **Domain Layer**: 包含最核心的 `Stock` Model、`StockRepository` 介面以及 `GetStocksUseCase`。不依賴任何外部框架。
- **Data Layer**: 負責資料獲取。透過 `TwseApi` (Retrofit) 抓取網路數據，並在 `StockRepositoryImpl` 中進行資料轉換與合併。
- **Presentation Layer**: 採用 **MVVM** 模式。
    - **ViewModel**: 使用 `StateFlow` 與 `combine` 即時計算 UI 狀態。
    - **UI (Compose)**: 使用 Jetpack Compose 打造現代化、反應迅速的宣告式 UI。

## 🛠️ 使用技術

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Networking**: Retrofit 2 + OkHttp 3 + Gson
- **Async**: Kotlin Coroutines & Flow
- **Dependency Injection**: Manual DI (Factory pattern)
- **Unit Testing**: JUnit 4 + Mockito-Kotlin + Coroutines Test
- **UI Testing**: Compose Test Rule

## 📦 如何開始

1. 克隆此專案：`git clone <repo-url>`
2. 在 Android Studio 中開啟專案。
3. 點擊 **Run 'app'** 即可在模擬器或實體裝置上運行。

---
*本程式僅供學習與技術交流使用，投資請審慎評估風險。*
