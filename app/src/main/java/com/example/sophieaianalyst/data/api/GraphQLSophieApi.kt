package com.example.sophieaianalyst.data.api

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.api.Query
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.example.sophieaianalyst.data.ApolloClientProvider
import com.example.sophieaianalyst.data.graphql.BatchStocksQuery
import com.example.sophieaianalyst.data.graphql.SearchStocksQuery
import com.example.sophieaianalyst.data.graphql.SentimentAgentQuery
import com.example.sophieaianalyst.data.graphql.SophieAnalysisQuery
import com.example.sophieaianalyst.data.graphql.StockDetailQuery
import com.example.sophieaianalyst.data.graphql.TechnicalAgentQuery
import com.example.sophieaianalyst.data.graphql.TrendingStocksQuery
import com.example.sophieaianalyst.data.graphql.ValueAgentQuery
import com.example.sophieaianalyst.model.AgentSignal
import com.example.sophieaianalyst.model.Fundamentals
import com.example.sophieaianalyst.model.NewsHeadline
import com.example.sophieaianalyst.model.Sentiment
import com.example.sophieaianalyst.model.SophieAnalysis
import com.example.sophieaianalyst.model.Stock
import com.example.sophieaianalyst.model.StockDetail
import com.example.sophieaianalyst.model.Technicals

/**
 * Apollo GraphQL implementation of SophieApiService
 */
class GraphQLSophieApi(
    private var apolloClient: ApolloClient
) : SophieApiService {
    private val TAG = "GraphQLSophieApi"
    
    /**
     * Execute a GraphQL query with retry logic for connection issues
     */
    private suspend inline fun <reified T : Query.Data> executeQuery(query: Query<T>): ApolloResponse<T> {
        try {
            // Try to execute the query
            Log.d(TAG, "Executing GraphQL query: ${query.javaClass.simpleName}")
            return apolloClient.query(query).execute()
        } catch (e: ApolloException) {
            // Log the error and try alternative URLs
            Log.e(TAG, "GraphQL error: ${e.message}", e)
            
            // Try with alternative URLs
            val newClient = ApolloClientProvider.tryNextServerUrl()
            if (newClient != null) {
                apolloClient = newClient
                return apolloClient.query(query).execute()
            }
            
            throw Exception("Failed to connect to any GraphQL server endpoints", e)
        }
    }
    
    /**
     * Get trending stocks from GraphQL API
     */
    override suspend fun getTrendingStocks(): List<Stock> {
        try {
            val response = executeQuery(TrendingStocksQuery())
            
            // Handle errors
            if (response.hasErrors()) {
                Log.e(TAG, "Error fetching trending stocks: ${response.errors?.firstOrNull()?.message}")
                throw Exception("Error fetching trending stocks: ${response.errors?.firstOrNull()?.message}")
            }
            
            // Get trending ticker symbols
            val tickers = response.data?.coveredTickers?.mapNotNull { it?.ticker } ?: emptyList()
            if (tickers.isEmpty()) return emptyList()
            
            Log.d(TAG, "Found ${tickers.size} trending tickers")
            
            // Fetch batch data for these tickers
            val batchResponse = executeQuery(
                BatchStocksQuery(
                    tickers = tickers,
                    start_date = Optional.present(getCurrentDate()),
                    end_date = Optional.present(getCurrentDate())
                )
            )
            
            if (batchResponse.hasErrors()) {
                Log.e(TAG, "Error fetching batch stocks: ${batchResponse.errors?.firstOrNull()?.message}")
                throw Exception("Error fetching batch stocks: ${batchResponse.errors?.firstOrNull()?.message}")
            }
            
            // Map GraphQL response to domain model
            return batchResponse.data?.batchStocks?.mapNotNull { it?.let { batchStock ->
                // Get latest price from prices list
                val latestPrice = batchStock.prices.lastOrNull()
                
                Stock(
                    ticker = batchStock.ticker,
                    name = batchStock.company.name,
                    price = latestPrice?.close?.toDouble() ?: 0.0,
                    change = 0.0, // Need to calculate change from previous day
                    sophieScore = batchStock.latestSophieAnalysis?.overall_score?.toInt() ?: 50,
                    color = getColorFromSignal(batchStock.latestSophieAnalysis?.signal ?: "NEUTRAL")
                )
            }} ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch trending stocks", e)
            // Return empty list for error fallback
            return emptyList()
        }
    }
    
    /**
     * Get stock details by ticker from GraphQL API
     */
    override suspend fun getStockDetail(ticker: String): StockDetail {
        val response = executeQuery(StockDetailQuery(ticker = ticker))
        
        // Handle errors
        if (response.hasErrors()) {
            throw Exception("Error fetching stock detail: ${response.errors?.firstOrNull()?.message}")
        }
        
        // Extract data from multiple queries
        val stockData = response.data?.stock
        val fundamentalsData = response.data?.latestFundamentals
        val technicalsData = response.data?.latestTechnicals
        val sophieAnalysisData = response.data?.latestSophieAnalysis
        
        if (stockData == null) {
            throw Exception("No data found for ticker $ticker")
        }
            
        // Map GraphQL response to domain model
        return StockDetail(
            ticker = stockData.company.ticker,
            name = stockData.company.name,
            price = 0.0, // Need to fetch from batch or price history
            change = 0.0, // Need to calculate
            sophieAnalysis = mapSophieAnalysis(sophieAnalysisData),
            fundamentals = fundamentalsData?.let { mapFundamentals(it) },
            technicals = technicalsData?.let { mapTechnicals(it) },
            sentiment = null, // Need to implement sentiment mapping
            agentSignals = getAgentSignals(ticker)
        )
    }
    
    /**
     * Get SOPHIE analysis for a stock from GraphQL API
     */
    override suspend fun getSophieAnalysis(ticker: String): SophieAnalysis {
        val response = executeQuery(SophieAnalysisQuery(ticker = ticker))
        
        // Handle errors
        if (response.hasErrors()) {
            throw Exception("Error fetching SOPHIE analysis: ${response.errors?.firstOrNull()?.message}")
        }
        
        // Extract SOPHIE analysis data
        val analysis = response.data?.latestSophieAnalysis
            ?: throw Exception("No SOPHIE analysis found for ticker $ticker")
            
        // Map GraphQL response to domain model
        return mapSophieAnalysis(analysis)
    }
    
    /**
     * Get AI agent signals for a stock from GraphQL API
     */
    override suspend fun getAgentSignals(ticker: String): List<AgentSignal> {
        // Get signals from individual agent queries
        val agentSignals = mutableListOf<AgentSignal>()
        
        try {
            // Value agent
            val valueResponse = executeQuery(ValueAgentQuery(ticker = ticker))
            if (!valueResponse.hasErrors()) {
                valueResponse.data?.latestAgentSignal?.let { 
                    agentSignals.add(mapAgentSignal(it, "Value Agent"))
                }
            }
            
            // Technical agent
            val technicalResponse = executeQuery(TechnicalAgentQuery(ticker = ticker))
            if (!technicalResponse.hasErrors()) {
                technicalResponse.data?.latestAgentSignal?.let { 
                    agentSignals.add(mapAgentSignal(it, "Technical Agent"))
                }
            }
            
            // Sentiment agent
            val sentimentResponse = executeQuery(SentimentAgentQuery(ticker = ticker))
            if (!sentimentResponse.hasErrors()) {
                sentimentResponse.data?.latestAgentSignal?.let { 
                    agentSignals.add(mapAgentSignal(it, "Sentiment Agent"))
                }
            }
            
            return agentSignals
        } catch (e: Exception) {
            throw Exception("Error fetching agent signals: ${e.message}")
        }
    }
    
    /**
     * Search for stocks by query from GraphQL API
     */
    override suspend fun searchStocks(query: String): List<Stock> {
        if (query.isEmpty()) return emptyList()
        
        val response = executeQuery(SearchStocksQuery(query = query))
        
        // Handle errors
        if (response.hasErrors()) {
            throw Exception("Error searching stocks: ${response.errors?.firstOrNull()?.message}")
        }
        
        // Get search results
        val searchResults = response.data?.searchStocks?.filterNotNull() ?: emptyList()
        if (searchResults.isEmpty()) return emptyList()
        
        // Map to tickers for batch query
        val tickers = searchResults.map { it.ticker }
        
        // Fetch batch data for these tickers
        val batchResponse = executeQuery(
            BatchStocksQuery(
                tickers = tickers,
                start_date = Optional.present(getCurrentDate()),
                end_date = Optional.present(getCurrentDate())
            )
        )
        
        if (batchResponse.hasErrors()) {
            throw Exception("Error fetching batch stocks: ${batchResponse.errors?.firstOrNull()?.message}")
        }
        
        // Map GraphQL response to domain model
        return batchResponse.data?.batchStocks?.mapNotNull { it?.let { batchStock ->
            // Get latest price from prices list
            val latestPrice = batchStock.prices.lastOrNull()
            
            Stock(
                ticker = batchStock.ticker,
                name = batchStock.company.name,
                price = latestPrice?.close?.toDouble() ?: 0.0,
                change = 0.0, // Need to calculate change from previous day
                sophieScore = batchStock.latestSophieAnalysis?.overall_score?.toInt() ?: 50,
                color = getColorFromSignal(batchStock.latestSophieAnalysis?.signal ?: "NEUTRAL")
            )
        }} ?: emptyList()
    }
    
    /**
     * Map GraphQL SophieAnalysis to domain model
     */
    private fun mapSophieAnalysis(analysis: SophieAnalysisQuery.LatestSophieAnalysis?): SophieAnalysis {
        if (analysis == null) {
            return SophieAnalysis(
                signal = "NEUTRAL",
                confidence = 50,
                overallScore = 50,
                reasoning = "No analysis available",
                shortTermOutlook = "Unknown",
                mediumTermOutlook = "Unknown", 
                longTermOutlook = "Unknown",
                bullishFactors = emptyList(),
                bearishFactors = emptyList(),
                risks = emptyList()
            )
        }
        
        return SophieAnalysis(
            signal = analysis.signal,
            confidence = analysis.confidence.toInt(),
            overallScore = analysis.overall_score.toInt(),
            reasoning = analysis.reasoning ?: "",
            shortTermOutlook = analysis.short_term_outlook ?: "",
            mediumTermOutlook = analysis.medium_term_outlook ?: "",
            longTermOutlook = analysis.long_term_outlook ?: "",
            bullishFactors = analysis.bullish_factors?.filterNotNull() ?: emptyList(),
            bearishFactors = analysis.bearish_factors?.filterNotNull() ?: emptyList(),
            risks = analysis.risks?.filterNotNull() ?: emptyList()
        )
    }
    
    /**
     * Map GraphQL SophieAnalysis from StockDetail to domain model
     */
    private fun mapSophieAnalysis(analysis: StockDetailQuery.LatestSophieAnalysis?): SophieAnalysis {
        if (analysis == null) {
            return SophieAnalysis(
                signal = "NEUTRAL",
                confidence = 50,
                overallScore = 50,
                reasoning = "No analysis available",
                shortTermOutlook = "Unknown",
                mediumTermOutlook = "Unknown", 
                longTermOutlook = "Unknown",
                bullishFactors = emptyList(),
                bearishFactors = emptyList(),
                risks = emptyList()
            )
        }
        
        return SophieAnalysis(
            signal = analysis.signal,
            confidence = analysis.confidence.toInt(),
            overallScore = analysis.overall_score.toInt(),
            reasoning = analysis.reasoning ?: "",
            shortTermOutlook = analysis.short_term_outlook ?: "",
            mediumTermOutlook = analysis.medium_term_outlook ?: "",
            longTermOutlook = analysis.long_term_outlook ?: "",
            bullishFactors = analysis.bullish_factors?.filterNotNull() ?: emptyList(),
            bearishFactors = analysis.bearish_factors?.filterNotNull() ?: emptyList(),
            risks = analysis.risks?.filterNotNull() ?: emptyList()
        )
    }
    
    /**
     * Map GraphQL Fundamentals to domain model
     */
    private fun mapFundamentals(fundamentals: StockDetailQuery.LatestFundamentals): Fundamentals {
        return Fundamentals(
            peRatio = fundamentals.pe_ratio,
            eps = fundamentals.earnings_per_share,
            dividendYield = null, // Not directly available
            marketCap = null, // Need from company facts
            revenue = null, // Not directly available
            grossMargin = null, // Not directly available
            operatingMargin = fundamentals.overall_signal.toDoubleOrNull(),
            netIncomeMargin = null, // Not directly available
            debtToEquity = fundamentals.debt_to_equity
        )
    }
    
    /**
     * Map GraphQL Technicals to domain model
     */
    private fun mapTechnicals(technicals: StockDetailQuery.LatestTechnicals): Technicals {
        return Technicals(
            macd = null, // Not directly available
            rsi = technicals.rsi_14,
            sma50 = null, // Not directly available
            sma200 = null, // Not directly available
            fiftyTwoWeekHigh = null, // Not directly available
            fiftyTwoWeekLow = null, // Not directly available
            volume = technicals.volume_ratio.toLong(), // This is an approximation
            averageVolume = null // Not directly available
        )
    }
    
    /**
     * Map GraphQL AgentSignal to domain model
     */
    private fun mapAgentSignal(agentSignal: ValueAgentQuery.LatestAgentSignal, displayName: String): AgentSignal {
        return AgentSignal(
            agent = agentSignal.agent,
            agentDisplayName = displayName,
            signal = agentSignal.signal,
            reasoning = agentSignal.reasoning ?: "",
            confidence = agentSignal.confidence.toInt(),
            date = agentSignal.biz_date
        )
    }
    
    /**
     * Map GraphQL AgentSignal to domain model
     */
    private fun mapAgentSignal(agentSignal: TechnicalAgentQuery.LatestAgentSignal, displayName: String): AgentSignal {
        return AgentSignal(
            agent = agentSignal.agent,
            agentDisplayName = displayName,
            signal = agentSignal.signal,
            reasoning = agentSignal.reasoning ?: "",
            confidence = agentSignal.confidence.toInt(),
            date = agentSignal.biz_date
        )
    }
    
    /**
     * Map GraphQL AgentSignal to domain model
     */
    private fun mapAgentSignal(agentSignal: SentimentAgentQuery.LatestAgentSignal, displayName: String): AgentSignal {
        return AgentSignal(
            agent = agentSignal.agent,
            agentDisplayName = displayName,
            signal = agentSignal.signal,
            reasoning = agentSignal.reasoning ?: "",
            confidence = agentSignal.confidence.toInt(),
            date = agentSignal.biz_date
        )
    }
    
    /**
     * Helper method to convert signal to color
     */
    private fun getColorFromSignal(signal: String): String {
        return when (signal.uppercase()) {
            "STRONG_BUY", "BUY" -> "#4CAF50" // Green
            "HOLD", "NEUTRAL" -> "#FFC107" // Yellow
            "SELL", "STRONG_SELL" -> "#F44336" // Red
            else -> "#9E9E9E" // Grey
        }
    }
    
    /**
     * Helper method to get current date in YYYY-MM-DD format
     */
    private fun getCurrentDate(): String {
        val now = java.time.LocalDate.now()
        return now.toString() // Returns in YYYY-MM-DD format
    }
    
    /**
     * Helper method to convert String to Double or null
     */
    private fun String.toDoubleOrNull(): Double? {
        return try {
            this.toDouble()
        } catch (e: Exception) {
            null
        }
    }
} 