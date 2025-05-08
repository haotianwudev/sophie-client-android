package com.example.sophieaianalyst.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sophieaianalyst.model.AgentSignal
import com.example.sophieaianalyst.ui.theme.BearishRed
import com.example.sophieaianalyst.ui.theme.BullishGreen
import com.example.sophieaianalyst.ui.theme.NeutralAmber
import com.example.sophieaianalyst.ui.theme.SophieTheme

/**
 * AgentSignalCard component displays an AI agent's signal for a stock
 */
@Composable
fun AgentSignalCard(
    agentSignal: AgentSignal,
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
            // Agent header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Agent avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(getAgentColor(agentSignal.agent)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = agentSignal.agentDisplayName.first().toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Agent name and signal
                Column {
                    Text(
                        text = agentSignal.agentDisplayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Confidence: ${agentSignal.confidence}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        val (signalColor, signalIcon) = getSignalColorAndIcon(agentSignal.signal)
                        
                        Surface(
                            color = signalColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = signalIcon,
                                    contentDescription = null,
                                    tint = signalColor,
                                    modifier = Modifier.size(12.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(4.dp))
                                
                                Text(
                                    text = agentSignal.signal.capitalize(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = signalColor
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Date
                Text(
                    text = agentSignal.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Agent reasoning
            Text(
                text = agentSignal.reasoning,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Get color for an agent based on their identifier
 */
private fun getAgentColor(agent: String): Color {
    return when (agent) {
        "warren_buffett" -> Color(0xFF1E88E5) // Blue
        "cathie_wood" -> Color(0xFF43A047)    // Green
        "ben_graham" -> Color(0xFF5E35B1)     // Deep Purple
        "stanley_druckenmiller" -> Color(0xFFE53935) // Red
        "charlie_munger" -> Color(0xFF6D4C41)  // Brown
        else -> Color(0xFF757575)             // Grey
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
 * Capitalize the first letter of a string
 */
private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

@Preview(showBackground = true)
@Composable
fun AgentSignalCardPreview() {
    SophieTheme {
        AgentSignalCard(
            agentSignal = AgentSignal(
                agent = "warren_buffett",
                agentDisplayName = "Warren Buffett",
                signal = "bullish",
                reasoning = "Strong brand moat, consistent cash flows, and reasonable valuation compared to other tech giants.",
                confidence = 85,
                date = "2025-05-05"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AgentSignalCardBearishPreview() {
    SophieTheme {
        AgentSignalCard(
            agentSignal = AgentSignal(
                agent = "ben_graham",
                agentDisplayName = "Benjamin Graham",
                signal = "bearish",
                reasoning = "Current price significantly exceeds intrinsic value based on traditional metrics. Speculation appears to be driving prices rather than fundamental analysis.",
                confidence = 80,
                date = "2025-05-03"
            )
        )
    }
} 