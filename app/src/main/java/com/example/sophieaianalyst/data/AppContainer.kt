package com.example.sophieaianalyst.data

import android.content.Context
import com.example.sophieaianalyst.data.api.GraphQLSophieApi
import com.example.sophieaianalyst.data.api.MockSophieApi
import com.example.sophieaianalyst.data.api.SophieApiService
import com.example.sophieaianalyst.data.repository.StockRepository
import com.example.sophieaianalyst.data.repository.StockRepositoryImpl

/**
 * Container for dependencies
 */
interface AppContainer {
    val stockRepository: StockRepository
}

/**
 * Default implementation of [AppContainer]
 */
class DefaultAppContainer(private val applicationContext: Context) : AppContainer {
    // Using the real GraphQL API instead of mock data
    private val useMockApi = false
    
    private val apiService: SophieApiService by lazy {
        if (useMockApi) {
            MockSophieApi()
        } else {
            val apolloClient = ApolloClientProvider.createApolloClient()
            GraphQLSophieApi(apolloClient)
        }
    }
    
    override val stockRepository: StockRepository by lazy {
        StockRepositoryImpl(apiService, applicationContext)
    }
} 