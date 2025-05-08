package com.example.sophieaianalyst.ui.screens.diagnostics

import android.os.Build
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.example.sophieaianalyst.data.ApolloClientProvider
import com.example.sophieaianalyst.ui.screens.home.HomeViewModel
import kotlinx.coroutines.launch
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Enumeration

@Composable
fun ConnectionDiagnosticsScreen(
    homeViewModel: HomeViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var networkInfo by remember { mutableStateOf("Loading network information...") }
    var testResults by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        networkInfo = getNetworkInfo()
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "GraphQL Connection Diagnostics",
                style = MaterialTheme.typography.headlineMedium
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Device Information",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
                Text("Android: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
                Text("Product: ${Build.PRODUCT}")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Network Information",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(networkInfo)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "GraphQL Server URLs",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // List the server URLs from ApolloClientProvider
                ApolloClientProvider.getServerUrls().forEachIndexed { index, url ->
                    Text("${index + 1}. $url")
                }
                
                Text(
                    text = "Current URL: ${ApolloClientProvider.getCurrentServerUrl()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                scope.launch {
                    testResults = "Testing connection..."
                    try {
                        homeViewModel.testServerConnection()
                        testResults += "\nConnection test initiated. Check Logcat for results."
                    } catch (e: Exception) {
                        testResults += "\nError: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test GraphQL Connection")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (testResults.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Test Results",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(testResults)
                }
            }
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