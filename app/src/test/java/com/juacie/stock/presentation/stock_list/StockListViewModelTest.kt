package com.juacie.stock.presentation.stock_list

import com.juacie.stock.domain.model.Stock
import com.juacie.stock.domain.usecase.GetStocksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class StockListViewModelTest {

    private lateinit var viewModel: StockListViewModel
    private lateinit var getStocksUseCase: GetStocksUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getStocksUseCase = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `stocks state should be updated when use case emits new list`() = runTest {
        val mockStocks = listOf(
            Stock("2330", "TSMC", 600.0, 10.0, 1.69)
        )
        whenever(getStocksUseCase()).thenReturn(flowOf(mockStocks))

        viewModel = StockListViewModel(getStocksUseCase)
        
        // Start collecting stocks to trigger the StateFlow (since it's WhileSubscribed)
        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.stocks.collect {}
        }

        advanceUntilIdle()

        assertEquals(mockStocks, viewModel.stocks.value)
        collectJob.cancel()
    }

    @Test
    fun `search query should filter stocks`() = runTest {
        val mockStocks = listOf(
            Stock("2330", "TSMC", 600.0, 10.0, 1.69),
            Stock("2317", "Hon Hai", 100.0, 2.0, 2.04)
        )
        whenever(getStocksUseCase()).thenReturn(flowOf(mockStocks))

        viewModel = StockListViewModel(getStocksUseCase)
        
        // Start collecting stocks
        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.stocks.collect {}
        }
        
        advanceUntilIdle()

        viewModel.onSearchQueryChange("TSMC")
        advanceUntilIdle()

        assertEquals(1, viewModel.stocks.value.size)
        assertEquals("2330", viewModel.stocks.value[0].symbol)
        
        collectJob.cancel()
    }
}
