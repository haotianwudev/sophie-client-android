package com.example.sophieaianalyst

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.sophieaianalyst.ui.screens.details.DetailsScreen
import com.example.sophieaianalyst.ui.theme.SophieTheme

/**
 * Activity for deep linking to stock details
 */
class StockDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Extract ticker from the URL
        val uri = intent.data
        val path = uri?.pathSegments?.lastOrNull() ?: ""
        val ticker = if (path.isEmpty()) {
            // If no ticker provided, use a default (or you could finish() the activity)
            "AAPL" 
        } else {
            path.uppercase()
        }
        
        setContent {
            SophieTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DetailsScreen(
                        ticker = ticker,
                        onBackClick = { finish() }
                    )
                }
            }
        }
    }
} 