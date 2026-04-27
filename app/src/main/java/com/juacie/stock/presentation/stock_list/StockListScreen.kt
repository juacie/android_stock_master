package com.juacie.stock.presentation.stock_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juacie.stock.domain.model.Stock
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockListScreen(viewModel: StockListViewModel) {
    val stocks by viewModel.stocks.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val sortType by viewModel.sortType.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }
    var selectedStockForDetails by remember { mutableStateOf<Stock?>(null) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("StockMaster") },
                    actions = {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sort by Symbol") },
                                onClick = {
                                    viewModel.onSortTypeChange(SortType.SYMBOL)
                                    showSortMenu = false
                                },
                                leadingIcon = { if(sortType == SortType.SYMBOL) Icon(Icons.Default.Check, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort by Price") },
                                onClick = {
                                    viewModel.onSortTypeChange(SortType.PRICE)
                                    showSortMenu = false
                                },
                                leadingIcon = { if(sortType == SortType.PRICE) Icon(Icons.Default.Check, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort by % Change") },
                                onClick = {
                                    viewModel.onSortTypeChange(SortType.CHANGE_PERCENT)
                                    showSortMenu = false
                                },
                                leadingIcon = { if(sortType == SortType.CHANGE_PERCENT) Icon(Icons.Default.Check, null) }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    )
                )
                
                // Search Bar
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    placeholder = { Text("Search by name or symbol...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    )
                )

                // Tabs
                PrimaryTabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { viewModel.onTabSelected(0) },
                        text = { Text("All Stocks") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { viewModel.onTabSelected(1) },
                        text = { Text("Favorites") }
                    )
                }
            }
        }
    ) { padding ->
        if (stocks.isEmpty() && searchQuery.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No results found for \"$searchQuery\"")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(stocks, key = { it.symbol }) { stock ->
                    StockItem(
                        stock = stock,
                        onFavoriteClick = { viewModel.toggleFavorite(stock.symbol) },
                        onClick = { selectedStockForDetails = stock }
                    )
                    HorizontalDivider()
                }
            }
        }

        if (selectedStockForDetails != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedStockForDetails = null },
                sheetState = sheetState
            ) {
                StockDetailContent(stock = selectedStockForDetails!!)
            }
        }
    }
}

@Composable
fun StockItem(stock: Stock, onFavoriteClick: () -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (stock.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (stock.isFavorite) Color.Red else Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = stock.symbol,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = stock.name,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    maxLines = 1
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$${String.format(Locale.getDefault(), "%.2f", stock.price)}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            val changeColor = if (stock.change >= 0) Color.Green else Color.Red
            val changeSign = if (stock.change >= 0) "+" else ""
            Text(
                text = "$changeSign${String.format(Locale.getDefault(), "%.2f", stock.change)} ($changeSign${String.format(Locale.getDefault(), "%.2f", stock.changePercentage)}%)",
                color = changeColor,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun StockDetailContent(stock: Stock) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = stock.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = stock.symbol,
            fontSize = 16.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "$${String.format(Locale.getDefault(), "%.2f", stock.price)}",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            val changeColor = if (stock.change >= 0) Color.Green else Color.Red
            val changeSign = if (stock.change >= 0) "+" else ""
            Text(
                text = "$changeSign${String.format(Locale.getDefault(), "%.2f", stock.change)} ($changeSign${String.format(Locale.getDefault(), "%.2f", stock.changePercentage)}%)",
                color = changeColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
        
        DetailRow("開盤價", stock.open ?: "-", "最高價", stock.high ?: "-")
        DetailRow("最低價", stock.low ?: "-", "成交量", stock.volume ?: "-")
        DetailRow("本益比", stock.peRatio ?: "-", "殖利率", "${stock.dividendYield ?: "-"} %")
        DetailRow("股價淨值比", stock.pbRatio ?: "-", "月平均價", stock.monthlyAveragePrice ?: "-")
    }
}

@Composable
fun DetailRow(label1: String, value1: String, label2: String, value2: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label1, color = Color.Gray, fontSize = 12.sp)
            Text(text = value1, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text(text = label2, color = Color.Gray, fontSize = 12.sp)
            Text(text = value2, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
