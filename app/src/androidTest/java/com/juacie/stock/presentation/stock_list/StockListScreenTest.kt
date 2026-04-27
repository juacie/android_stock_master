package com.juacie.stock.presentation.stock_list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.juacie.stock.domain.model.Stock
import com.juacie.stock.domain.usecase.GetStocksUseCase
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class StockListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: StockListViewModel
    private lateinit var getStocksUseCase: GetStocksUseCase

    @Before
    fun setUp() {
        getStocksUseCase = mock()
        val mockStocks = listOf(
            Stock("2330", "TSMC", 600.0, 10.0, 1.69)
        )
        whenever(getStocksUseCase()).thenReturn(flowOf(mockStocks))
        viewModel = StockListViewModel(getStocksUseCase)
    }

    @Test
    fun stockItem_isDisplayed() {
        composeTestRule.setContent {
            StockListScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("TSMC").assertIsDisplayed()
        composeTestRule.onNodeWithText("2330").assertIsDisplayed()
    }

    @Test
    fun clickingStockItem_showsBottomSheet() {
        composeTestRule.setContent {
            StockListScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("TSMC").performClick()

        // Check for detail content in bottom sheet
        composeTestRule.onNodeWithText("開盤價").assertIsDisplayed()
    }
}
