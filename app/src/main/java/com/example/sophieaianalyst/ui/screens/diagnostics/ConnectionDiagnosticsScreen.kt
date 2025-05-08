package com.example.sophieaianalyst.ui.screens.diagnostics

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sophieaianalyst.data.ApolloClientProvider
import com.example.sophieaianalyst.ui.screens.home.HomeViewModel
import kotlinx.coroutines.launch
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Enumeration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Connection diagnostics screen for troubleshooting API connectivity issues
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionDiagnosticsScreen(
    homeViewModel: HomeViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var logs by remember { mutableStateOf<List<String>>(emptyList()) }
    var lastTestTime by remember { mutableStateOf("Not run yet") }
    var connectionStatus by remember { mutableStateOf("Unknown") }
    var currentEndpoint by remember { mutableStateOf("Unknown") }
    
    // Get current endpoint information on initial composition
    LaunchedEffect(key1 = Unit) {
        currentEndpoint = ApolloClientProvider.getCurrentEndpoint() ?: "No endpoint configured"
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Connection Diagnostics",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
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
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Current connection status
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Connection Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Status: $connectionStatus",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "Last Test: $lastTestTime",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "Current Endpoint: $currentEndpoint",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // Test connection button
            Button(
                onClick = {
                    coroutineScope.launch {
                        // Clear previous logs
                        logs = listOf("Starting connection test...")
                        
                        try {
                            // Update time
                            lastTestTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                            
                            // Test the connection
                            connectionStatus = "Testing..."
                            logs = logs + "Fetching trending stocks..."
                            
                            // Use the HomeViewModel to test the connection
                            try {
                                // Call may throw an exception, which is caught in our outer try-catch
                                homeViewModel.testGraphQLConnection()
                                connectionStatus = "Connected"
                                logs = logs + "Connection successful!"
                            } catch (e: Exception) {
                                throw e  // Re-throw to be caught by outer try-catch
                            }
                            
                            currentEndpoint = ApolloClientProvider.getCurrentEndpoint() ?: "Unknown"
                        } catch (e: Exception) {
                            // Update status on failure
                            connectionStatus = "Failed"
                            logs = logs + "Connection failed: ${e.message}"
                            logs = logs + "Stack trace: ${e.stackTraceToString().take(500)}..."
                        }
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null
                )
                Text(
                    text = "Test Connection",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            // Logs section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Connection Logs",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = logs.joinToString("\n"),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            
            // API Endpoints section 
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "API Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val endpoints = ApolloClientProvider.getConfiguredEndpoints()
                    Text(
                        text = "Available Endpoints:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    for (endpoint in endpoints) {
                        Text(
                            text = "• $endpoint",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun getNetworkInfo(): String {
    val sb = StringBuilder()
    
    try {
        // Get all network interfaces
        val interfaces: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val networkInterface = interfaces.nextElement()
            
            // Skip loopback and inactive interfaces
            if (networkInterface.isLoopback || !networkInterface.isUp) continue
            
            sb.append("Interface: ${networkInterface.displayName}\n")
            
            val addresses: Enumeration<InetAddress> = networkInterface.inetAddresses
            while (addresses.hasMoreElements()) {
                val address = addresses.nextElement()
                sb.append("  Address: ${address.hostAddress}\n")
            }
            
            sb.append("\n")
        }
        
        // Check if localhost is reachable
        sb.append("Checking localhost reachability:\n")
        try {
            val localhostReachable = InetAddress.getByName("localhost").isReachable(3000)
            sb.append("  localhost: ${if (localhostReachable) "Reachable" else "Not reachable"}\n")
        } catch (e: Exception) {
            sb.append("  localhost: Error - ${e.message}\n")
        }
        
        try {
            val ipReachable = InetAddress.getByName("127.0.0.1").isReachable(3000)
            sb.append("  127.0.0.1: ${if (ipReachable) "Reachable" else "Not reachable"}\n")
        } catch (e: Exception) {
            sb.append("  127.0.0.1: Error - ${e.message}\n")
        }
        
        try {
            val emulatorHostReachable = InetAddress.getByName("10.0.2.2").isReachable(3000)
            sb.append("  10.0.2.2: ${if (emulatorHostReachable) "Reachable" else "Not reachable"}\n")
        } catch (e: Exception) {
            sb.append("  10.0.2.2: Error - ${e.message}\n")
        }
    } catch (e: Exception) {
        sb.append("Error getting network info: ${e.message}")
    }
    
    return sb.toString()
} 