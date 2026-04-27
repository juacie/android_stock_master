package com.juacie.stock.presentation.stock_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juacie.stock.domain.model.Stock
import com.juacie.stock.domain.usecase.GetStocksUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortType {
    SYMBOL, PRICE, CHANGE_PERCENT
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

    private val _selectedTab = MutableStateFlow(0) // 0: All, 1: Favorites
    val selectedTab = _selectedTab.asStateFlow()

    val stocks: StateFlow<List<Stock>> = combine(
        _allStocks, _searchQuery, _sortType, _favoriteIds, _selectedTab
    ) { allStocks, query, sort, favorites, tabIndex ->
        var filteredList = allStocks.map { it.copy(isFavorite = favorites.contains(it.symbol)) }

        // Tab filtering
        if (tabIndex == 1) {
            filteredList = filteredList.filter { it.isFavorite }
        }

        // Search filtering
        if (query.isNotBlank()) {
            filteredList = filteredList.filter {
                it.symbol.contains(query, ignoreCase = true) ||
                        it.name.contains(query, ignoreCase = true)
            }
        }

        // Sorting
        when (sort) {
            SortType.SYMBOL -> filteredList.sortedBy { it.symbol }
            SortType.PRICE -> filteredList.sortedByDescending { it.price }
            SortType.CHANGE_PERCENT -> filteredList.sortedByDescending { it.changePercentage }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadStocks()
    }

    private fun loadStocks() {
        viewModelScope.launch {
            getStocksUseCase().collect {
                _allStocks.value = it
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
