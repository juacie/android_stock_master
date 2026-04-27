package com.juacie.stock.presentation.stock_list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.juacie.stock.domain.model.Stock
import com.juacie.stock.domain.usecase.GetStocksUseCase
import com.juacie.stock.ui.theme.StockTheme
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
            StockTheme {
                StockListScreen(viewModel = viewModel)
            }
        }

        // Wait for the item to appear since it loads from StateFlow
        composeTestRule.onNodeWithText("TSMC").assertIsDisplayed()
        composeTestRule.onNodeWithText("2330").assertIsDisplayed()
    }

    @Test
    fun clickingStockItem_showsBottomSheet() {
        composeTestRule.setContent {
            StockTheme {
                StockListScreen(viewModel = viewModel)
            }
        }

        // Wait for list to load
        composeTestRule.onNodeWithText("TSMC").assertIsDisplayed()
        composeTestRule.onNodeWithText("TSMC").performClick()

        // Wait for bottom sheet to appear in the tree
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(hasTestTag("stock_detail_content")).fetchSemanticsNodes().isNotEmpty()
        }
        
        // Try to scroll to "開盤價" within the bottom sheet if it's not displayed
        composeTestRule.onNodeWithText("開盤價").performScrollTo().assertIsDisplayed()
    }
}
