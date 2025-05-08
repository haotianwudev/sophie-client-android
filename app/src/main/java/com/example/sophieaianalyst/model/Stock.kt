package com.example.sophieaianalyst.model

data class Stock(
    val ticker: String,
    val name: String,
    val price: Double,
    val change: Double,
    val sophieScore: Int,
    val color: String = ""
)

data class StockDetail(
    val ticker: String,
    val name: String,
    val price: Double,
    val change: Double,
    val sophieAnalysis: SophieAnalysis,
    val fundamentals: Fundamentals? = null,
    val technicals: Technicals? = null,
    val sentiment: Sentiment? = null,
    val agentSignals: List<AgentSignal> = emptyList()
)

data class SophieAnalysis(
    val signal: String,
    val confidence: Int,
    val overallScore: Int,
    val reasoning: String,
    val shortTermOutlook: String,
    val mediumTermOutlook: String,
    val longTermOutlook: String,
    val bullishFactors: List<String>,
    val bearishFactors: List<String>,
    val risks: List<String>
)

data class Fundamentals(
    val peRatio: Double?,
    val eps: Double?,
    val dividendYield: Double?,
    val marketCap: Long?,
    val revenue: Long?,
    val grossMargin: Double?,
    val operatingMargin: Double?,
    val netIncomeMargin: Double?,
    val debtToEquity: Double?
)

data class Technicals(
    val macd: Double?,
    val rsi: Double?,
    val sma50: Double?,
    val sma200: Double?,
    val fiftyTwoWeekHigh: Double?,
    val fiftyTwoWeekLow: Double?,
    val volume: Long?,
    val averageVolume: Long?
)

data class Sentiment(
    val analystConsensus: String,
    val analystRating: Double?,
    val analystPriceTarget: Double?,
    val newsHeadlines: List<NewsHeadline>,
    val socialMediaSentiment: Double?
)

data class NewsHeadline(
    val title: String,
    val source: String,
    val date: String,
    val url: String,
    val sentiment: String
)

data class AgentSignal(
    val agent: String,
    val agentDisplayName: String,
    val signal: String,
    val reasoning: String,
    val confidence: Int,
    val date: String
) 