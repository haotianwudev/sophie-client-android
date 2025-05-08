package com.example.sophieaianalyst.ui.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sophieaianalyst.SophieApplication
import com.example.sophieaianalyst.data.repository.StockRepository
import com.example.sophieaianalyst.model.AgentSignal
import com.example.sophieaianalyst.model.SophieAnalysis
import com.example.sophieaianalyst.model.StockDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the details screen
 */
data class DetailsUiState(
    val isLoading: Boolean = true,
    val stockDetail: StockDetail? = null,
    val error: String? = null,
    val selectedTab: AnalysisTab = AnalysisTab.SOPHIE
)

/**
 * Analysis tabs for the details screen
 */
enum class AnalysisTab {
    SOPHIE, TECHNICAL, FUNDAMENTAL, SENTIMENT, AGENTS
}

/**
 * ViewModel for the details screen
 */
class DetailsViewModel(
    private val stockRepository: StockRepository,
    private val ticker: String
) : ViewModel() {
    
    // UI state for the details screen
    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()
    
    init {
        // Load stock details when the ViewModel is created
        loadStockDetails()
    }
    
    /**
     * Load stock details from the repository
     */
    fun loadStockDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val stockDetail = stockRepository.getStockDetail(ticker)
                _uiState.update { it.copy(isLoading = false, stockDetail = stockDetail) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = "Failed to load stock details: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    /**
     * Select an analysis tab
     */
    fun selectTab(tab: AnalysisTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
    
    /**
     * Toggle watchlist status (to be implemented in a future version)
     */
    fun toggleWatchlist() {
        // This would be implemented in a future version to add/remove the stock from a watchlist
    }
    
    /**
     * Factory for creating DetailsViewModel with a ticker parameter
     */
    companion object {
        fun factory(ticker: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as SophieApplication)
                val stockRepository = application.container.stockRepository
                DetailsViewModel(stockRepository, ticker)
            }
        }
    }
} 