package com.example.sophieaianalyst.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sophieaianalyst.model.Stock
import com.example.sophieaianalyst.ui.components.SearchBar
import com.example.sophieaianalyst.ui.components.StockCard
import com.example.sophieaianalyst.ui.components.StockCardShimmer
import com.example.sophieaianalyst.ui.theme.SophieTheme

/**
 * Home screen composable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStockClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "SOPHIE AI Stock Analyst",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.loadTrendingStocks() }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
            }
        }
    ) { paddingValues ->
        HomeContent(
            uiState = uiState,
            onStockClick = onStockClick,
            onSearchQueryChange = viewModel::onSearchQueryChanged,
            onClearSearch = viewModel::clearSearch,
            onToggleBookmark = viewModel::toggleBookmark,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

/**
 * Home screen content
 */
@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onStockClick: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onToggleBookmark: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        if (uiState.isLoading) {
            // Loading state
            LoadingContent(modifier = Modifier.fillMaxSize())
        } else if (uiState.error != null) {
            // Error state
            ErrorContent(
                message = uiState.error,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Content loaded
            Column(modifier = Modifier.fillMaxSize()) {
                // Search bar
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onClearQuery = onClearSearch
                )
                
                // If we're searching, show search results, otherwise show bookmarks + trending
                if (uiState.isSearchActive) {
                    SearchResultsList(
                        stocks = uiState.searchResults,
                        onStockClick = onStockClick,
                        isBookmarked = { ticker -> 
                            uiState.bookmarkedStocks.any { it.ticker == ticker }
                        },
                        onToggleBookmark = onToggleBookmark,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    StocksList(
                        trendingStocks = uiState.trendingStocks,
                        bookmarkedStocks = uiState.bookmarkedStocks,
                        onStockClick = onStockClick,
                        isBookmarked = { ticker -> 
                            uiState.bookmarkedStocks.any { it.ticker == ticker }
                        },
                        onToggleBookmark = onToggleBookmark,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

/**
 * Loading content with shimmer effect
 */
@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Show 5 shimmer loading cards
        items(5) {
            StockCardShimmer()
        }
    }
}

/**
 * Error content
 */
@Composable
private fun ErrorContent(
    message: String,
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
    }
}

/**
 * Search results list
 */
@Composable
private fun SearchResultsList(
    stocks: List<Stock>,
    onStockClick: (String) -> Unit,
    isBookmarked: (String) -> Boolean,
    onToggleBookmark: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (stocks.isEmpty()) {
        // Empty search results
        Column(
            modifier = modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No matching stocks found",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Try a different search term.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Search Results",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Stock cards
            items(stocks) { stock ->
                StockCard(
                    stock = stock,
                    onClick = { onStockClick(stock.ticker) },
                    isBookmarked = isBookmarked(stock.ticker),
                    onToggleBookmark = onToggleBookmark
                )
            }
        }
    }
}

/**
 * Stocks list content
 */
@Composable
private fun StocksList(
    trendingStocks: List<Stock>,
    bookmarkedStocks: List<Stock>,
    onStockClick: (String) -> Unit,
    isBookmarked: (String) -> Boolean,
    onToggleBookmark: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Introduction header
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Hello, Investor",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "SOPHIE is analyzing the market to find opportunities for you",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Bookmarked stocks
        if (bookmarkedStocks.isNotEmpty()) {
            item {
                Text(
                    text = "Your Bookmarks",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            items(bookmarkedStocks) { stock ->
                StockCard(
                    stock = stock,
                    onClick = { onStockClick(stock.ticker) },
                    isBookmarked = true,
                    onToggleBookmark = onToggleBookmark
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Trending Stocks",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            item {
                Text(
                    text = "Trending Stocks",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        
        // Trending stock cards
        items(trendingStocks) { stock ->
            StockCard(
                stock = stock,
                onClick = { onStockClick(stock.ticker) },
                isBookmarked = isBookmarked(stock.ticker),
                onToggleBookmark = onToggleBookmark
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SophieTheme {
        HomeContent(
            uiState = HomeUiState(
                isLoading = false,
                trendingStocks = listOf(
                    Stock(
                        ticker = "AAPL",
                        name = "Apple Inc.",
                        price = 187.68,
                        change = 3.45,
                        sophieScore = 78
                    ),
                    Stock(
                        ticker = "MSFT",
                        name = "Microsoft Corporation",
                        price = 405.32,
                        change = 2.87,
                        sophieScore = 85
                    )
                ),
                bookmarkedStocks = listOf(
                    Stock(
                        ticker = "AAPL",
                        name = "Apple Inc.",
                        price = 187.68,
                        change = 3.45,
                        sophieScore = 78
                    )
                )
            ),
            onStockClick = {},
            onSearchQueryChange = {},
            onClearSearch = {},
            onToggleBookmark = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenLoadingPreview() {
    SophieTheme {
        HomeContent(
            uiState = HomeUiState(isLoading = true),
            onStockClick = {},
            onSearchQueryChange = {},
            onClearSearch = {},
            onToggleBookmark = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenErrorPreview() {
    SophieTheme {
        HomeContent(
            uiState = HomeUiState(
                isLoading = false,
                error = "Failed to load trending stocks"
            ),
            onStockClick = {},
            onSearchQueryChange = {},
            onClearSearch = {},
            onToggleBookmark = {}
        )
    }
} 