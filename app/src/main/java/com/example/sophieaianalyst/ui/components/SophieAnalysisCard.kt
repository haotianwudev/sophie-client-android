package com.example.sophieaianalyst.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sophieaianalyst.model.SophieAnalysis
import com.example.sophieaianalyst.ui.theme.BearishRed
import com.example.sophieaianalyst.ui.theme.BullishGreen
import com.example.sophieaianalyst.ui.theme.NeutralAmber
import com.example.sophieaianalyst.ui.theme.ScoreHigh
import com.example.sophieaianalyst.ui.theme.ScoreLow
import com.example.sophieaianalyst.ui.theme.ScoreMediumHigh
import com.example.sophieaianalyst.ui.theme.ScoreMediumLow
import com.example.sophieaianalyst.ui.theme.SophieTheme

/**
 * SophieAnalysisCard component displays SOPHIE's analysis of a stock
 */
@Composable
fun SophieAnalysisCard(
    analysis: SophieAnalysis,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with SOPHIE avatar and title
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF6750A4),
                                    Color(0xFF9C27B0)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "S",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Title and subtitle
                Column {
                    Text(
                        text = "SOPHIE Analysis",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Confidence: ${analysis.confidence}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        val (signalColor, signalIcon) = getSignalColorAndIcon(analysis.signal)
                        
                        Surface(
                            color = signalColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = signalIcon,
                                    contentDescription = null,
                                    tint = signalColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(4.dp))
                                
                                Text(
                                    text = analysis.signal.capitalize(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = signalColor
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // SOPHIE Score
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(getScoreGradient(analysis.overallScore)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${analysis.overallScore}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    Text(
                        text = "SOPHIE SCORE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Analysis content
            Text(
                text = analysis.reasoning,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Outlook sections
            OutlookSection(
                title = "Short-term Outlook",
                content = analysis.shortTermOutlook
            )
            
            OutlookSection(
                title = "Medium-term Outlook",
                content = analysis.mediumTermOutlook
            )
            
            OutlookSection(
                title = "Long-term Outlook",
                content = analysis.longTermOutlook
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Factors sections
            Divider()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Key Factors",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Bullish factors
            FactorsList(
                title = "Bullish Factors",
                factors = analysis.bullishFactors,
                icon = Icons.Outlined.Check,
                iconColor = BullishGreen
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Bearish factors
            FactorsList(
                title = "Bearish Factors",
                factors = analysis.bearishFactors,
                icon = Icons.Outlined.Clear,
                iconColor = BearishRed
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Risks
            FactorsList(
                title = "Risks to Watch",
                factors = analysis.risks,
                icon = Icons.Outlined.Warning,
                iconColor = NeutralAmber
            )
        }
    }
}

/**
 * Outlook section for short, medium, and long-term analysis
 */
@Composable
private fun OutlookSection(
    title: String,
    content: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(12.dp))
    }
}

/**
 * List of factors with icons
 */
@Composable
private fun FactorsList(
    title: String,
    factors: List<String>,
    icon: ImageVector,
    iconColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            factors.forEach { factor ->
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = factor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/**
 * Get signal color and icon based on signal value
 */
private fun getSignalColorAndIcon(signal: String): Pair<Color, ImageVector> {
    return when (signal.lowercase()) {
        "bullish" -> Pair(BullishGreen, Icons.Filled.ArrowUpward)
        "bearish" -> Pair(BearishRed, Icons.Filled.ArrowDownward)
        else -> Pair(NeutralAmber, Icons.Filled.ArrowForward)
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
 * Capitalize the first letter of a string
 */
private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

@Preview(showBackground = true)
@Composable
fun SophieAnalysisCardPreview() {
    SophieTheme {
        SophieAnalysisCard(
            analysis = SophieAnalysis(
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
            )
        )
    }
} 