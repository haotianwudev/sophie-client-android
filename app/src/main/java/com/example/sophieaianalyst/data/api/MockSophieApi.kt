package com.example.sophieaianalyst.data.api

import com.example.sophieaianalyst.model.AgentSignal
import com.example.sophieaianalyst.model.Fundamentals
import com.example.sophieaianalyst.model.NewsHeadline
import com.example.sophieaianalyst.model.Sentiment
import com.example.sophieaianalyst.model.SophieAnalysis
import com.example.sophieaianalyst.model.Stock
import com.example.sophieaianalyst.model.StockDetail
import com.example.sophieaianalyst.model.Technicals
import kotlinx.coroutines.delay

/**
 * Mock implementation of SophieApiService for demo purposes
 */
class MockSophieApi : SophieApiService {
    // Stock colors for UI styling - matching web version
    private val stockColors = mapOf(
        "AAPL" to "from-blue-500 to-cyan-500",
        "MSFT" to "from-emerald-500 to-green-500",
        "NVDA" to "from-green-500 to-lime-500",
        "AMZN" to "from-red-500 to-orange-500",
        "GOOGL" to "from-blue-400 to-indigo-500",
        "META" to "from-blue-500 to-blue-700",
        "TSLA" to "from-red-500 to-red-700"
    )
    
    // Mock trending stocks
    private val trendingStocks = listOf(
        Stock(
            ticker = "AAPL",
            name = "Apple Inc.",
            price = 187.68,
            change = 3.45,
            sophieScore = 78,
            color = stockColors["AAPL"] ?: ""
        ),
        Stock(
            ticker = "MSFT",
            name = "Microsoft Corporation",
            price = 405.32,
            change = 2.87,
            sophieScore = 85,
            color = stockColors["MSFT"] ?: ""
        ),
        Stock(
            ticker = "NVDA",
            name = "NVIDIA Corporation",
            price = 953.86,
            change = 5.12,
            sophieScore = 92,
            color = stockColors["NVDA"] ?: ""
        ),
        Stock(
            ticker = "AMZN",
            name = "Amazon.com, Inc.",
            price = 182.75,
            change = 1.98,
            sophieScore = 81,
            color = stockColors["AMZN"] ?: ""
        ),
        Stock(
            ticker = "GOOGL",
            name = "Alphabet Inc.",
            price = 167.21,
            change = -0.75,
            sophieScore = 76,
            color = stockColors["GOOGL"] ?: ""
        ),
        Stock(
            ticker = "META",
            name = "Meta Platforms, Inc.",
            price = 478.22,
            change = 1.25,
            sophieScore = 83,
            color = stockColors["META"] ?: ""
        ),
        Stock(
            ticker = "TSLA",
            name = "Tesla, Inc.",
            price = 187.11,
            change = -2.45,
            sophieScore = 65,
            color = stockColors["TSLA"] ?: ""
        )
    )
    
    // Mock stock details
    private val stockDetails = mapOf(
        "AAPL" to StockDetail(
            ticker = "AAPL",
            name = "Apple Inc.",
            price = 187.68,
            change = 3.45,
            sophieAnalysis = SophieAnalysis(
                signal = "bullish",
                confidence = 78,
                overallScore = 78,
                reasoning = "Apple continues to demonstrate strong ecosystem lock-in, high margins, and loyal customer base. While facing competition in hardware, their services segment shows promising growth.",
                shortTermOutlook = "Stable with potential upside from new product releases",
                mediumTermOutlook = "Positive with services growth offsetting hardware maturation",
                longTermOutlook = "Strong core business with new market opportunities",
                bullishFactors = listOf(
                    "Strong services revenue growth",
                    "High customer loyalty and retention",
                    "Robust ecosystem integration",
                    "Healthy cash reserves"
                ),
                bearishFactors = listOf(
                    "Maturing smartphone market",
                    "Regulatory scrutiny on App Store",
                    "Competition from other tech giants"
                ),
                risks = listOf(
                    "Supply chain disruptions",
                    "Slowing innovation cycle",
                    "Increased competition in wearables"
                )
            ),
            fundamentals = Fundamentals(
                peRatio = 30.56,
                eps = 6.14,
                dividendYield = 0.53,
                marketCap = 2940000000000,
                revenue = 383000000000,
                grossMargin = 43.8,
                operatingMargin = 29.2,
                netIncomeMargin = 25.3,
                debtToEquity = 1.86
            ),
            technicals = Technicals(
                macd = 1.24,
                rsi = 65.2,
                sma50 = 182.4,
                sma200 = 175.8,
                fiftyTwoWeekHigh = 196.38,
                fiftyTwoWeekLow = 143.90,
                volume = 64500000,
                averageVolume = 58200000
            ),
            sentiment = Sentiment(
                analystConsensus = "buy",
                analystRating = 4.2,
                analystPriceTarget = 205.0,
                newsHeadlines = listOf(
                    NewsHeadline(
                        title = "Apple's services business continues to impress",
                        source = "MarketWatch",
                        date = "2025-05-05",
                        url = "https://example.com/news1",
                        sentiment = "positive"
                    ),
                    NewsHeadline(
                        title = "New iPhone models expected to drive upgrade cycle",
                        source = "Bloomberg",
                        date = "2025-05-03",
                        url = "https://example.com/news2",
                        sentiment = "positive"
                    ),
                    NewsHeadline(
                        title = "Concerns about Apple's hardware sales in emerging markets",
                        source = "Reuters",
                        date = "2025-05-01",
                        url = "https://example.com/news3",
                        sentiment = "negative"
                    )
                ),
                socialMediaSentiment = 72.8
            ),
            agentSignals = getAgentSignalsForStock("AAPL")
        ),
        "NVDA" to StockDetail(
            ticker = "NVDA",
            name = "NVIDIA Corporation",
            price = 953.86,
            change = 5.12,
            sophieAnalysis = SophieAnalysis(
                signal = "bullish",
                confidence = 92,
                overallScore = 92,
                reasoning = "NVIDIA continues to dominate AI chipsets market with strong data center growth and leadership in GPU technology. Sustained demand for AI infrastructure provides strong tailwinds.",
                shortTermOutlook = "Strong momentum to continue with AI investments",
                mediumTermOutlook = "Dominant position in growing AI infrastructure market",
                longTermOutlook = "Well-positioned for sustained AI and computing revolution",
                bullishFactors = listOf(
                    "Market leadership in AI chips",
                    "Strong data center revenue growth",
                    "Expanding product ecosystem",
                    "High margins and strong cash flow"
                ),
                bearishFactors = listOf(
                    "High valuation multiples",
                    "Cyclical semiconductor industry",
                    "Increasing competition from AMD and Intel"
                ),
                risks = listOf(
                    "Potential AI spending slowdown",
                    "Manufacturing capacity constraints",
                    "Regulatory concerns over Arm acquisition"
                )
            ),
            fundamentals = Fundamentals(
                peRatio = 68.2,
                eps = 14.0,
                dividendYield = 0.03,
                marketCap = 2350000000000,
                revenue = 60500000000,
                grossMargin = 74.5,
                operatingMargin = 54.3,
                netIncomeMargin = 45.8,
                debtToEquity = 0.41
            ),
            technicals = Technicals(
                macd = 22.4,
                rsi = 72.1,
                sma50 = 908.6,
                sma200 = 780.2,
                fiftyTwoWeekHigh = 983.45,
                fiftyTwoWeekLow = 640.20,
                volume = 42300000,
                averageVolume = 36800000
            ),
            sentiment = Sentiment(
                analystConsensus = "strong buy",
                analystRating = 4.8,
                analystPriceTarget = 1050.0,
                newsHeadlines = listOf(
                    NewsHeadline(
                        title = "NVIDIA sees continued strong demand for AI chips",
                        source = "Financial Times",
                        date = "2025-05-05",
                        url = "https://example.com/news1",
                        sentiment = "positive"
                    ),
                    NewsHeadline(
                        title = "NVIDIA announces next-generation GPU architecture",
                        source = "TechCrunch",
                        date = "2025-05-02",
                        url = "https://example.com/news2",
                        sentiment = "positive"
                    ),
                    NewsHeadline(
                        title = "Supply constraints may limit NVIDIA's growth",
                        source = "Wall Street Journal",
                        date = "2025-04-28",
                        url = "https://example.com/news3",
                        sentiment = "negative"
                    )
                ),
                socialMediaSentiment = 85.6
            ),
            agentSignals = getAgentSignalsForStock("NVDA")
        )
    )
    
    /**
     * Get trending stocks with a slight delay to simulate network request
     */
    override suspend fun getTrendingStocks(): List<Stock> {
        delay(800) // Add delay to simulate network request
        return trendingStocks
    }
    
    /**
     * Get stock details by ticker with a slight delay to simulate network request
     */
    override suspend fun getStockDetail(ticker: String): StockDetail {
        delay(1000) // Add delay to simulate network request
        return stockDetails[ticker] ?: createDefaultStockDetail(ticker)
    }
    
    /**
     * Get SOPHIE analysis for a stock
     */
    override suspend fun getSophieAnalysis(ticker: String): SophieAnalysis {
        delay(700) // Add delay to simulate network request
        return stockDetails[ticker]?.sophieAnalysis ?: createDefaultSophieAnalysis(ticker)
    }
    
    /**
     * Get AI agent signals for a stock
     */
    override suspend fun getAgentSignals(ticker: String): List<AgentSignal> {
        delay(600) // Add delay to simulate network request
        return getAgentSignalsForStock(ticker)
    }
    
    /**
     * Search for stocks by query
     */
    override suspend fun searchStocks(query: String): List<Stock> {
        delay(500) // Add delay to simulate network request
        
        // If query is empty, return empty list
        if (query.isEmpty()) return emptyList()
        
        // Filter trending stocks that match the query (case insensitive)
        val queryLower = query.lowercase()
        return trendingStocks.filter { 
            it.ticker.lowercase().contains(queryLower) || 
            it.name.lowercase().contains(queryLower)
        }
    }
    
    /**
     * Generate agent signals for a given stock
     */
    private fun getAgentSignalsForStock(ticker: String): List<AgentSignal> {
        // Define pre-made signals for each agent
        return listOf(
            AgentSignal(
                agent = "warren_buffett",
                agentDisplayName = "Warren Buffett",
                signal = if (ticker == "AAPL") "bullish" else if (ticker == "NVDA") "neutral" else "neutral",
                reasoning = if (ticker == "AAPL") 
                    "Strong brand moat, consistent cash flows, and reasonable valuation compared to other tech giants." 
                else if (ticker == "NVDA") 
                    "Excellent business, but current valuation requires perfect execution for many years. Prefer to wait for better entry point."
                else 
                    "Need more information about long-term competitive advantages and current valuation metrics.",
                confidence = if (ticker == "AAPL") 85 else if (ticker == "NVDA") 65 else 60,
                date = "2025-05-05"
            ),
            AgentSignal(
                agent = "cathie_wood",
                agentDisplayName = "Cathie Wood",
                signal = if (ticker == "AAPL") "neutral" else if (ticker == "NVDA") "bullish" else "neutral",
                reasoning = if (ticker == "AAPL") 
                    "Solid company but lacks the disruptive innovation we seek. Looking for more transformative technology plays." 
                else if (ticker == "NVDA") 
                    "Central to the AI revolution. Their chips power the infrastructure needed for the next wave of technological innovation."
                else 
                    "Evaluating potential disruptive capabilities within their product roadmap.",
                confidence = if (ticker == "AAPL") 55 else if (ticker == "NVDA") 95 else 60,
                date = "2025-05-04"
            ),
            AgentSignal(
                agent = "ben_graham",
                agentDisplayName = "Benjamin Graham",
                signal = if (ticker == "AAPL") "neutral" else if (ticker == "NVDA") "bearish" else "neutral",
                reasoning = if (ticker == "AAPL") 
                    "Solid fundamentals but trading above my preferred margin of safety. Would consider on significant pullbacks." 
                else if (ticker == "NVDA") 
                    "Current price significantly exceeds intrinsic value based on traditional metrics. Speculation appears to be driving prices rather than fundamental analysis."
                else 
                    "Need to establish a clearer margin of safety based on tangible asset value and earnings consistency.",
                confidence = if (ticker == "AAPL") 70 else if (ticker == "NVDA") 80 else 60,
                date = "2025-05-03"
            ),
            AgentSignal(
                agent = "stanley_druckenmiller",
                agentDisplayName = "Stanley Druckenmiller",
                signal = if (ticker == "AAPL") "bullish" else if (ticker == "NVDA") "bullish" else "neutral",
                reasoning = if (ticker == "AAPL") 
                    "Current market positioning and services growth trajectory suggest continued outperformance relative to broader market." 
                else if (ticker == "NVDA") 
                    "Leading technology paradigm shift in AI with strong institutional adoption. Momentum likely to continue despite high valuations."
                else 
                    "Monitoring macro trends that could impact business model and institutional positioning.",
                confidence = if (ticker == "AAPL") 75 else if (ticker == "NVDA") 90 else 60,
                date = "2025-05-02"
            ),
            AgentSignal(
                agent = "charlie_munger",
                agentDisplayName = "Charlie Munger",
                signal = if (ticker == "AAPL") "bullish" else if (ticker == "NVDA") "neutral" else "neutral",
                reasoning = if (ticker == "AAPL") 
                    "Exceptional business quality with durable competitive advantages. Patience with high-quality businesses is rewarded over time." 
                else if (ticker == "NVDA") 
                    "Brilliant business, but remember that price is what you pay, value is what you get. Current price requires extraordinarily high expectations to be met."
                else 
                    "Avoid complex predictions when simple observations will suffice. More analysis needed on competitive position.",
                confidence = if (ticker == "AAPL") 85 else if (ticker == "NVDA") 60 else 60,
                date = "2025-05-01"
            )
        )
    }
    
    /**
     * Create a default stock detail for stocks not in our predefined list
     */
    private fun createDefaultStockDetail(ticker: String): StockDetail {
        return StockDetail(
            ticker = ticker,
            name = getCompanyNameFromTicker(ticker),
            price = 100.0 + (ticker.hashCode() % 900).toDouble(),
            change = -3.0 + (ticker.hashCode() % 12).toDouble(),
            sophieAnalysis = createDefaultSophieAnalysis(ticker),
            fundamentals = Fundamentals(
                peRatio = 20.0 + (ticker.hashCode() % 30).toDouble(),
                eps = 2.0 + (ticker.hashCode() % 10).toDouble(),
                dividendYield = 0.5 + (ticker.hashCode() % 3).toDouble() / 10,
                marketCap = 10000000000 + (ticker.hashCode() % 1000) * 10000000L,
                revenue = 5000000000 + (ticker.hashCode() % 500) * 10000000L,
                grossMargin = 30.0 + (ticker.hashCode() % 30).toDouble(),
                operatingMargin = 15.0 + (ticker.hashCode() % 20).toDouble(),
                netIncomeMargin = 10.0 + (ticker.hashCode() % 15).toDouble(),
                debtToEquity = 0.5 + (ticker.hashCode() % 2).toDouble()
            ),
            technicals = Technicals(
                macd = -2.0 + (ticker.hashCode() % 8).toDouble(),
                rsi = 30.0 + (ticker.hashCode() % 40).toDouble(),
                sma50 = 95.0 + (ticker.hashCode() % 855).toDouble(),
                sma200 = 90.0 + (ticker.hashCode() % 810).toDouble(),
                fiftyTwoWeekHigh = 120.0 + (ticker.hashCode() % 1000).toDouble(),
                fiftyTwoWeekLow = 80.0 + (ticker.hashCode() % 400).toDouble(),
                volume = 1000000L + (ticker.hashCode() % 10000000L),
                averageVolume = 900000L + (ticker.hashCode() % 9000000L)
            ),
            sentiment = Sentiment(
                analystConsensus = "neutral",
                analystRating = 3.0 + (ticker.hashCode() % 2).toDouble(),
                analystPriceTarget = 110.0 + (ticker.hashCode() % 1000).toDouble(),
                newsHeadlines = listOf(
                    NewsHeadline(
                        title = "$ticker reports quarterly earnings",
                        source = "Financial News",
                        date = "2025-05-02",
                        url = "https://example.com/news1",
                        sentiment = "neutral"
                    ),
                    NewsHeadline(
                        title = "Analyst upgrades $ticker on growth prospects",
                        source = "Market Insights",
                        date = "2025-04-28",
                        url = "https://example.com/news2",
                        sentiment = "positive"
                    ),
                    NewsHeadline(
                        title = "Challenges ahead for $ticker in competitive landscape",
                        source = "Industry Reports",
                        date = "2025-04-25",
                        url = "https://example.com/news3",
                        sentiment = "negative"
                    )
                ),
                socialMediaSentiment = 50.0 + (ticker.hashCode() % 30).toDouble()
            ),
            agentSignals = getAgentSignalsForStock(ticker)
        )
    }
    
    /**
     * Create a default SOPHIE analysis for stocks not in our predefined list
     */
    private fun createDefaultSophieAnalysis(ticker: String): SophieAnalysis {
        val score = 50 + (ticker.hashCode() % 40)
        val signal = when {
            score >= 70 -> "bullish"
            score <= 40 -> "bearish"
            else -> "neutral"
        }
        
        return SophieAnalysis(
            signal = signal,
            confidence = 60 + (ticker.hashCode() % 30),
            overallScore = score,
            reasoning = "Analysis based on available data shows mixed signals. $ticker shows some strengths in its market positioning but also faces industry headwinds.",
            shortTermOutlook = "Potential volatility with sector rotation",
            mediumTermOutlook = "Performance likely tied to broader industry trends",
            longTermOutlook = "Further analysis needed for long-term prospects",
            bullishFactors = listOf(
                "Competitive positioning in core markets",
                "Recent product innovations",
                "Potential for margin improvement"
            ),
            bearishFactors = listOf(
                "Increasing competition",
                "Margin pressures",
                "Macroeconomic headwinds"
            ),
            risks = listOf(
                "Regulatory changes",
                "Execution risks in strategic initiatives",
                "Supply chain disruptions"
            )
        )
    }
    
    /**
     * Get a company name from a ticker symbol
     */
    private fun getCompanyNameFromTicker(ticker: String): String {
        return when (ticker) {
            "AAPL" -> "Apple Inc."
            "MSFT" -> "Microsoft Corporation"
            "AMZN" -> "Amazon.com, Inc."
            "GOOGL" -> "Alphabet Inc."
            "GOOG" -> "Alphabet Inc."
            "META" -> "Meta Platforms, Inc."
            "TSLA" -> "Tesla, Inc."
            "NVDA" -> "NVIDIA Corporation"
            "NFLX" -> "Netflix, Inc."
            "PYPL" -> "PayPal Holdings, Inc."
            "INTC" -> "Intel Corporation"
            "AMD" -> "Advanced Micro Devices, Inc."
            "CRM" -> "Salesforce, Inc."
            "CSCO" -> "Cisco Systems, Inc."
            "ADBE" -> "Adobe Inc."
            else -> "$ticker Corporation"
        }
    }
} 