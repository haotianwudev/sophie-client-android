package com.example.sophieaianalyst.data.repository

import com.example.sophieaianalyst.model.AgentSignal
import com.example.sophieaianalyst.model.SophieAnalysis
import com.example.sophieaianalyst.model.Stock
import com.example.sophieaianalyst.model.StockDetail
import kotlinx.coroutines.flow.Flow

/**
 * Repository for stock data
 */
interface StockRepository {
    /**
     * Get trending stocks
     */
    suspend fun getTrendingStocks(): List<Stock>
    
    /**
     * Get stock details by ticker
     */
    suspend fun getStockDetail(ticker: String): StockDetail
    
    /**
     * Get SOPHIE analysis for a stock
     */
    suspend fun getSophieAnalysis(ticker: String): SophieAnalysis
    
    /**
     * Get AI agent signals for a stock
     */
    suspend fun getAgentSignals(ticker: String): List<AgentSignal>
    
    /**
     * Get bookmarked stocks
     */
    fun getBookmarkedStocks(): Flow<List<Stock>>
    
    /**
     * Bookmark a stock
     */
    suspend fun bookmarkStock(ticker: String)
    
    /**
     * Unbookmark a stock
     */
    suspend fun unbookmarkStock(ticker: String)
    
    /**
     * Check if a stock is bookmarked
     */
    suspend fun isStockBookmarked(ticker: String): Boolean
    
    /**
     * Search for stocks by query
     */
    suspend fun searchStocks(query: String): List<Stock>
} 