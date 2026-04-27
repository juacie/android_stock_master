package com.juacie.stock.presentation.stock_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juacie.stock.domain.model.Stock
import com.juacie.stock.domain.usecase.GetStocksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortType {
    SYMBOL, PRICE, CHANGE_PERCENT
}

sealed interface StockListUiState {
    object Loading : StockListUiState
    data class Success(val stocks: List<Stock>) : StockListUiState
    data class Error(val message: String) : StockListUiState
    object Empty : StockListUiState
}

class StockListViewModel(
    private val getStocksUseCase: GetStocksUseCase
) : ViewModel() {

    private val _allStocks = MutableStateFlow<List<Stock>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _sortType = MutableStateFlow(SortType.SYMBOL)
    val sortType = _sortType.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds = _favoriteIds.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab = _selectedTab.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    private val _errorMessage = MutableStateFlow<String?>(null)

    @OptIn(FlowPreview::class)
    private val debouncedSearchQuery = _searchQuery
        .debounce(300L)
        .distinctUntilChanged()

    // 負責處理過濾與排序的邏輯流
    private val filteredStocks: Flow<List<Stock>> = combine(
        _allStocks,
        debouncedSearchQuery,
        _sortType,
        _favoriteIds,
        _selectedTab
    ) { allStocks, query, sort, favorites, tabIndex ->
        var filteredStream = allStocks.asSequence()

        if (tabIndex == 1) {
            filteredStream = filteredStream.filter { favorites.contains(it.symbol) }
        }

        if (query.isNotBlank()) {
            filteredStream = filteredStream.filter {
                it.symbol.contains(query, ignoreCase = true) ||
                        it.name.contains(query, ignoreCase = true)
            }
        }

        val sortedList = when (sort) {
            SortType.SYMBOL -> filteredStream.sortedBy { it.symbol }
            SortType.PRICE -> filteredStream.sortedByDescending { it.price }
            SortType.CHANGE_PERCENT -> filteredStream.sortedByDescending { it.changePercentage }
        }.toList()

        sortedList.map { it.copy(isFavorite = favorites.contains(it.symbol)) }
    }.flowOn(Dispatchers.Default)

    // 負責整合 LCE 狀態的 UI 狀態流
    val uiState: StateFlow<StockListUiState> = combine(
        filteredStocks,
        _isLoading,
        _errorMessage,
        _searchQuery,
        _selectedTab
    ) { stocks, loading, error, query, tabIndex ->
        when {
            error != null -> StockListUiState.Error(error)
            loading && stocks.isEmpty() && _allStocks.value.isEmpty() -> StockListUiState.Loading
            stocks.isEmpty() -> {
                if (query.isNotBlank()) {
                    StockListUiState.Success(emptyList()) // 搜尋無結果，不顯示全域空狀態
                } else if (tabIndex == 1) {
                    StockListUiState.Empty // 自選股為空
                } else if (_allStocks.value.isEmpty()) {
                    StockListUiState.Empty // 真的沒資料
                } else {
                    StockListUiState.Success(emptyList())
                }
            }
            else -> StockListUiState.Success(stocks)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StockListUiState.Loading
    )

    init {
        loadStocks()
    }

    fun loadStocks() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                getStocksUseCase().collect {
                    _allStocks.value = it
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "網路連線異常，請檢查您的網路設定"
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSortTypeChange(type: SortType) {
        _sortType.value = type
    }

    fun toggleFavorite(symbol: String) {
        val current = _favoriteIds.value.toMutableSet()
        if (current.contains(symbol)) {
            current.remove(symbol)
        } else {
            current.add(symbol)
        }
        _favoriteIds.value = current
    }

    fun onTabSelected(index: Int) {
        _selectedTab.value = index
    }
}
