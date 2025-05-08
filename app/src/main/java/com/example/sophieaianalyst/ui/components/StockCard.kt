package com.example.sophieaianalyst.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sophieaianalyst.model.Stock
import com.example.sophieaianalyst.ui.theme.BullishGreen
import com.example.sophieaianalyst.ui.theme.BearishRed
import com.example.sophieaianalyst.ui.theme.ScoreHigh
import com.example.sophieaianalyst.ui.theme.ScoreMediumHigh
import com.example.sophieaianalyst.ui.theme.ScoreMediumLow
import com.example.sophieaianalyst.ui.theme.ScoreLow
import com.example.sophieaianalyst.ui.theme.SophieTheme

/**
 * StockCard component displays a stock item in a card layout
 */
@Composable
fun StockCard(
    stock: Stock,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isBookmarked: Boolean = false,
    onToggleBookmark: ((String) -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Colored header bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(getGradientBrush(stock.ticker))
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stock info
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stock.ticker,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        
                        // Bookmark button
                        if (onToggleBookmark != null) {
                            BookmarkButton(
                                isBookmarked = isBookmarked,
                                onToggleBookmark = { onToggleBookmark(stock.ticker) }
                            )
                        }
                    }
                    
                    Text(
                        text = stock.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "$${stock.price.formatToTwoDecimals()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val changeColor = if (stock.change >= 0) BullishGreen else BearishRed
                        val changeIcon = if (stock.change >= 0) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward
                        
                        Icon(
                            imageVector = changeIcon,
                            contentDescription = null,
                            tint = changeColor,
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = "${stock.change.formatToTwoDecimals()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = changeColor
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = "3-month change",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // SOPHIE Score
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(getScoreGradient(stock.sophieScore)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${stock.sophieScore}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    Text(
                        text = "SOPHIE SCORE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Get gradient brush based on ticker
 */
private fun getGradientBrush(ticker: String): Brush {
    return when (ticker) {
        "AAPL" -> Brush.horizontalGradient(listOf(Color(0xFF2196F3), Color(0xFF00BCD4)))
        "MSFT" -> Brush.horizontalGradient(listOf(Color(0xFF10B981), Color(0xFF22C55E)))
        "NVDA" -> Brush.horizontalGradient(listOf(Color(0xFF22C55E), Color(0xFF84CC16)))
        "AMZN" -> Brush.horizontalGradient(listOf(Color(0xFFEF4444), Color(0xFFF97316)))
        "GOOGL" -> Brush.horizontalGradient(listOf(Color(0xFF60A5FA), Color(0xFF6366F1)))
        "META" -> Brush.horizontalGradient(listOf(Color(0xFF2196F3), Color(0xFF1565C0)))
        "TSLA" -> Brush.horizontalGradient(listOf(Color(0xFFEF4444), Color(0xFFB91C1C)))
        else -> Brush.horizontalGradient(listOf(Color(0xFF6750A4), Color(0xFF8B7BC8)))
    }
}

/**
 * Get score gradient based on score value
 */
private fun getScoreGradient(score: Int): Brush {
    return when {
        score >= 80 -> Brush.linearGradient(listOf(ScoreHigh, ScoreHigh.copy(alpha = 0.8f)))
        score >= 60 -> Brush.linearGradient(listOf(ScoreMediumHigh, ScoreMediumHigh.copy(alpha = 0.8f)))
        score >= 40 -> Brush.linearGradient(listOf(ScoreMediumLow, ScoreMediumLow.copy(alpha = 0.8f)))
        else -> Brush.linearGradient(listOf(ScoreLow, ScoreLow.copy(alpha = 0.8f)))
    }
}

/**
 * Format Double to two decimal places
 */
fun Double.formatToTwoDecimals(): String {
    return String.format("%.2f", this)
}

@Preview(showBackground = true)
@Composable
fun StockCardPreview() {
    SophieTheme {
        StockCard(
            stock = Stock(
                ticker = "AAPL",
                name = "Apple Inc.",
                price = 187.68,
                change = 3.45,
                sophieScore = 78
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StockCardNegativePreview() {
    SophieTheme {
        StockCard(
            stock = Stock(
                ticker = "TSLA",
                name = "Tesla, Inc.",
                price = 187.11,
                change = -2.45,
                sophieScore = 45
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StockCardBookmarkedPreview() {
    SophieTheme {
        StockCard(
            stock = Stock(
                ticker = "AAPL",
                name = "Apple Inc.",
                price = 187.68,
                change = 3.45,
                sophieScore = 78
            ),
            onClick = {},
            isBookmarked = true,
            onToggleBookmark = {}
        )
    }
} 