package com.example.sophieaianalyst.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.sophieaianalyst.data.api.SophieApiService
import com.example.sophieaianalyst.model.AgentSignal
import com.example.sophieaianalyst.model.SophieAnalysis
import com.example.sophieaianalyst.model.Stock
import com.example.sophieaianalyst.model.StockDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.bookmarksDataStore by preferencesDataStore(name = "bookmarks")

/**
 * Implementation of [StockRepository]
 */
class StockRepositoryImpl(
    private val apiService: SophieApiService,
    private val context: Context
) : StockRepository {
    
    companion object {
        private fun bookmarkKey(ticker: String) = booleanPreferencesKey("bookmark_$ticker")
    }
    
    override suspend fun getTrendingStocks(): List<Stock> {
        return apiService.getTrendingStocks()
    }
    
    override suspend fun getStockDetail(ticker: String): StockDetail {
        return apiService.getStockDetail(ticker)
    }
    
    override suspend fun getSophieAnalysis(ticker: String): SophieAnalysis {
        return apiService.getSophieAnalysis(ticker)
    }
    
    override suspend fun getAgentSignals(ticker: String): List<AgentSignal> {
        return apiService.getAgentSignals(ticker)
    }
    
    override fun getBookmarkedStocks(): Flow<List<Stock>> {
        return context.bookmarksDataStore.data.map { preferences ->
            // Find all bookmarked tickers
            val bookmarkedTickers = preferences.asMap()
                .filter { (key, value) -> key.name.startsWith("bookmark_") && value == true }
                .map { (key, _) -> key.name.removePrefix("bookmark_") }
            
            // For each bookmarked ticker, fetch its stock data
            bookmarkedTickers.mapNotNull { ticker ->
                try {
                    val stockDetail = apiService.getStockDetail(ticker)
                    Stock(
                        ticker = stockDetail.ticker,
                        name = stockDetail.name,
                        price = stockDetail.price,
                        change = stockDetail.change,
                        sophieScore = stockDetail.sophieAnalysis.overallScore,
                        color = "" // Placeholder, could add a way to maintain colors
                    )
                } catch (e: Exception) {
                    null // Skip stocks that fail to load
                }
            }
        }
    }
    
    override suspend fun bookmarkStock(ticker: String) {
        context.bookmarksDataStore.edit { preferences ->
            preferences[bookmarkKey(ticker)] = true
        }
    }
    
    override suspend fun unbookmarkStock(ticker: String) {
        context.bookmarksDataStore.edit { preferences ->
            preferences[bookmarkKey(ticker)] = false
        }
    }
    
    override suspend fun isStockBookmarked(ticker: String): Boolean {
        return context.bookmarksDataStore.data.map { preferences ->
            preferences[bookmarkKey(ticker)] ?: false
        }.first()
    }
    
    override suspend fun searchStocks(query: String): List<Stock> {
        // If query is empty, return an empty list
        if (query.isBlank()) return emptyList()
        
        // Get all trending stocks as a base for search
        val allStocks = getTrendingStocks()
        
        // Filter stocks by query (ticker or name)
        return allStocks.filter { stock ->
            stock.ticker.contains(query, ignoreCase = true) || 
            stock.name.contains(query, ignoreCase = true)
        }
    }
} 