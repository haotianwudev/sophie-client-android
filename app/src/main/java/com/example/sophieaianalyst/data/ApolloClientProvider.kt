package com.example.sophieaianalyst.data

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Provider for Apollo GraphQL client
 */
object ApolloClientProvider {
    private const val TAG = "ApolloClientProvider"
    
    // Try multiple server options
    private val SERVER_URLS = listOf(
        "https://sophieai-graphql-api.onrender.com/graphql", // Demo endpoint
        "http://10.0.2.2:4000/graphql",      // Android emulator localhost
        "http://localhost:4000/graphql",      // Direct localhost
        "http://127.0.0.1:4000/graphql",      // Explicit IP
        "http://192.168.1.100:4000/graphql"   // Common local network IP (adjust as needed)
    )
    
    // Start with the first URL
    private var currentUrlIndex = 0
    private var currentServerUrl = SERVER_URLS[currentUrlIndex]
    private var lastConnectionStatus = "Not connected"
    private var connectionErrors = mutableListOf<String>()
    
    /**
     * Create a new Apollo client
     */
    fun createApolloClient(): ApolloClient {
        // Configure OkHttp client with extensive logging
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(TAG, "HTTP: $message")
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        
        Log.d(TAG, "Creating Apollo client with URL: $currentServerUrl")
        lastConnectionStatus = "Connecting to $currentServerUrl"
        
        // Build Apollo client
        return ApolloClient.Builder()
            .serverUrl(currentServerUrl)
            .okHttpClient(okHttpClient)
            .build()
    }
    
    /**
     * Try the next server URL
     * @return new Apollo client with the next URL, or null if all URLs have been tried
     */
    fun tryNextServerUrl(): ApolloClient? {
        connectionErrors.add("Failed to connect to $currentServerUrl")
        
        currentUrlIndex++
        if (currentUrlIndex >= SERVER_URLS.size) {
            Log.e(TAG, "All server URLs have been tried without success")
            lastConnectionStatus = "Failed - all endpoints exhausted"
            return null
        }
        
        currentServerUrl = SERVER_URLS[currentUrlIndex]
        Log.d(TAG, "Trying next server URL: $currentServerUrl")
        lastConnectionStatus = "Trying $currentServerUrl"
        return createApolloClient()
    }
    
    /**
     * Get the current endpoint (URL and status)
     * @return current endpoint description
     */
    fun getCurrentEndpoint(): String? {
        return currentServerUrl
    }
    
    /**
     * Get the list of configured endpoints
     * @return list of endpoint URLs
     */
    fun getConfiguredEndpoints(): List<String> {
        return SERVER_URLS
    }
    
    /**
     * Get connection status information
     * @return status information as a formatted string
     */
    fun getConnectionStatus(): String {
        return lastConnectionStatus
    }
    
    /**
     * Get connection errors
     * @return list of connection errors
     */
    fun getConnectionErrors(): List<String> {
        return connectionErrors
    }
    
    /**
     * Reset connection status
     */
    fun resetConnectionStatus() {
        connectionErrors.clear()
        lastConnectionStatus = "Not connected"
        currentUrlIndex = 0
        currentServerUrl = SERVER_URLS[currentUrlIndex]
    }
} 