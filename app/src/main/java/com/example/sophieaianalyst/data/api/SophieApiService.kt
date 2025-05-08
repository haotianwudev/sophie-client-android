package com.example.sophieaianalyst.data.api

import com.example.sophieaianalyst.model.AgentSignal
import com.example.sophieaianalyst.model.SophieAnalysis
import com.example.sophieaianalyst.model.Stock
import com.example.sophieaianalyst.model.StockDetail

/**
 * API Service interface for SOPHIE AI Analyst
 */
interface SophieApiService {
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
} 