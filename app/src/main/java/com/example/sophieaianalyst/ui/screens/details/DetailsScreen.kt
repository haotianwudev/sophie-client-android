package com.example.sophieaianalyst.ui.screens.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sophieaianalyst.model.AgentSignal
import com.example.sophieaianalyst.model.SophieAnalysis
import com.example.sophieaianalyst.model.StockDetail
import com.example.sophieaianalyst.ui.components.AgentSignalCard
import com.example.sophieaianalyst.ui.components.SophieAnalysisCard
import com.example.sophieaianalyst.ui.components.SophieAnalysisShimmer
import com.example.sophieaianalyst.ui.theme.BearishRed
import com.example.sophieaianalyst.ui.theme.BullishGreen
import com.example.sophieaianalyst.ui.theme.SophieTheme

/**
 * Stock details screen composable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    ticker: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = viewModel(factory = DetailsViewModel.factory(ticker))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = ticker,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        uiState.stockDetail?.name?.let { name ->
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.toggleWatchlist() }) {
                Icon(
                    imageVector = Icons.Default.BookmarkAdd,
                    contentDescription = "Add to Watchlist"
                )
            }
        }
    ) { paddingValues ->
        DetailsContent(
            uiState = uiState,
            onTabSelected = viewModel::selectTab,
            onRefresh = viewModel::loadStockDetails,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

/**
 * Details content
 */
@Composable
private fun DetailsContent(
    uiState: DetailsUiState,
    onTabSelected: (AnalysisTab) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Price and change information
        uiState.stockDetail?.let { stockDetail ->
            PriceInfoSection(stockDetail)
        }
        
        // Analysis Tabs
        ScrollableTabRow(
            selectedTabIndex = uiState.selectedTab.ordinal,
            edgePadding = 16.dp
        ) {
            AnalysisTab.values().forEach { tab ->
                Tab(
                    selected = uiState.selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                    text = { 
                        Text(
                            text = tab.name.lowercase().capitalize(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = getTabIcon(tab),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
            }
        }
        
        // Tab content
        Box(modifier = Modifier.weight(1f)) {
            if (uiState.isLoading) {
                // Loading state
                LoadingContent(modifier = Modifier.fillMaxSize())
            } else if (uiState.error != null) {
                // Error state
                ErrorContent(
                    message = uiState.error,
                    onRefresh = onRefresh,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (uiState.stockDetail == null) {
                // Empty state
                EmptyContent(
                    ticker = "this stock", 
                    onRefresh = onRefresh,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Content loaded
                TabContent(
                    stockDetail = uiState.stockDetail,
                    selectedTab = uiState.selectedTab,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Price information section
 */
@Composable
private fun PriceInfoSection(stockDetail: StockDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "$${stockDetail.price.formatToTwoDecimals()}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val changeColor = if (stockDetail.change >= 0) BullishGreen else BearishRed
                val changeIcon = if (stockDetail.change >= 0) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
                
                Icon(
                    imageVector = changeIcon,
                    contentDescription = null,
                    tint = changeColor,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = "${stockDetail.change.formatToTwoDecimals()}%",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = changeColor
                )
            }
        }
        
        Text(
            text = "3-month change | SOPHIE Score: ${stockDetail.sophieAnalysis.overallScore}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Get icon for a tab
 */
@Composable
private fun getTabIcon(tab: AnalysisTab): ImageVector {
    return when (tab) {
        AnalysisTab.SOPHIE -> Icons.Outlined.SmartToy
        AnalysisTab.TECHNICAL -> Icons.Outlined.Analytics
        AnalysisTab.FUNDAMENTAL -> Icons.Outlined.Assessment
        AnalysisTab.SENTIMENT -> Icons.Outlined.Mood
        AnalysisTab.AGENTS -> Icons.Outlined.Groups
    }
}

/**
 * Tab content based on selected tab
 */
@Composable
private fun TabContent(
    stockDetail: StockDetail,
    selectedTab: AnalysisTab,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (selectedTab) {
            AnalysisTab.SOPHIE -> {
                item {
                    SophieAnalysisCard(
                        analysis = stockDetail.sophieAnalysis
                    )
                }
            }
            
            AnalysisTab.TECHNICAL -> {
                item {
                    // For this demo, we'll just display a placeholder
                    TechnicalAnalysisPlaceholder()
                }
            }
            
            AnalysisTab.FUNDAMENTAL -> {
                item {
                    // For this demo, we'll just display a placeholder
                    FundamentalAnalysisPlaceholder()
                }
            }
            
            AnalysisTab.SENTIMENT -> {
                item {
                    // For this demo, we'll just display a placeholder
                    SentimentAnalysisPlaceholder()
                }
            }
            
            AnalysisTab.AGENTS -> {
                item {
                    Text(
                        text = "AI Agents Analysis",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "See how famous investing personalities might analyze this stock",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                items(stockDetail.agentSignals) { signal ->
                    AgentSignalCard(agentSignal = signal)
                }
            }
        }
    }
}

/**
 * Technical analysis placeholder
 */
@Composable
private fun TechnicalAnalysisPlaceholder() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Technical Analysis",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Technical analysis section is simplified for this mobile version.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Key metrics without charts are displayed to optimize for mobile.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Fundamental analysis placeholder
 */
@Composable
private fun FundamentalAnalysisPlaceholder() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Fundamental Analysis",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Fundamental analysis section shows key financial metrics.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "In a complete implementation, this would include P/E ratio, revenue growth, profit margins, and more.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Sentiment analysis placeholder
 */
@Composable
private fun SentimentAnalysisPlaceholder() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sentiment Analysis",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Sentiment analysis shows market perception of the stock.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "This would include analyst ratings, news sentiment, and social media sentiment metrics.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Loading content
 */
@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SophieAnalysisShimmer()
    }
}

/**
 * Error content
 */
@Composable
private fun ErrorContent(
    message: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Oops!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        IconButton(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

/**
 * Empty content
 */
@Composable
private fun EmptyContent(
    ticker: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Data Found",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "We couldn't find any data for $ticker.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        IconButton(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

/**
 * Format Double to two decimal places
 */
private fun Double.formatToTwoDecimals(): String {
    return String.format("%.2f", this)
}

/**
 * Capitalize the first letter of a string
 */
private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

// Previews are not included to save space, but would typically be added here 