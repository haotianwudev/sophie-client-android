package com.example.sophieaianalyst.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sophieaianalyst.SophieApplication
import com.example.sophieaianalyst.data.repository.StockRepository
import com.example.sophieaianalyst.model.Stock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the home screen
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val trendingStocks: List<Stock> = emptyList(),
    val bookmarkedStocks: List<Stock> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<Stock> = emptyList(),
    val isSearchActive: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for the home screen
 */
class HomeViewModel(
    private val stockRepository: StockRepository
) : ViewModel() {
    
    // UI state for the home screen
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        // Load trending stocks when the ViewModel is created
        loadTrendingStocks()
        // Load bookmarked stocks
        loadBookmarkedStocks()
    }
    
    /**
     * Load trending stocks from the repository
     */
    fun loadTrendingStocks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val stocks = stockRepository.getTrendingStocks()
                _uiState.update { it.copy(isLoading = false, trendingStocks = stocks) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = "Failed to load trending stocks: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    /**
     * Load bookmarked stocks from the repository
     */
    private fun loadBookmarkedStocks() {
        viewModelScope.launch {
            stockRepository.getBookmarkedStocks().collectLatest { bookmarkedStocks ->
                _uiState.update { 
                    it.copy(bookmarkedStocks = bookmarkedStocks) 
                }
            }
        }
    }
    
    /**
     * Toggle bookmark status for a stock
     */
    fun toggleBookmark(ticker: String) {
        viewModelScope.launch {
            val isCurrentlyBookmarked = stockRepository.isStockBookmarked(ticker)
            if (isCurrentlyBookmarked) {
                stockRepository.unbookmarkStock(ticker)
            } else {
                stockRepository.bookmarkStock(ticker)
            }
        }
    }
    
    /**
     * Update search query and search for matching stocks
     */
    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(searchQuery = query, isSearchActive = query.isNotBlank()) }
            
            if (query.isBlank()) {
                _uiState.update { it.copy(searchResults = emptyList(), isSearchActive = false) }
                return@launch
            }
            
            try {
                val results = stockRepository.searchStocks(query)
                _uiState.update { it.copy(searchResults = results) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        searchResults = emptyList(),
                        error = "Failed to search stocks: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    /**
     * Clear search and return to main view
     */
    fun clearSearch() {
        _uiState.update { 
            it.copy(
                searchQuery = "",
                searchResults = emptyList(),
                isSearchActive = false
            )
        }
    }
    
    /**
     * Test GraphQL server connection
     */
    fun testServerConnection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                Log.d("HomeViewModel", "Testing GraphQL server connection")
                val stocks = stockRepository.getTrendingStocks()
                if (stocks.isNotEmpty()) {
                    Log.d("HomeViewModel", "Successfully connected to GraphQL server!")
                } else {
                    Log.d("HomeViewModel", "Connection test returned empty data")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to connect to GraphQL server: ${e.message}", e)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    /**
     * Factory for creating HomeViewModel
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as SophieApplication)
                val stockRepository = application.container.stockRepository
                HomeViewModel(stockRepository)
            }
        }
    }
} 