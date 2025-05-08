package com.example.sophieaianalyst.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sophieaianalyst.ui.screens.details.DetailsScreen
import com.example.sophieaianalyst.ui.screens.diagnostics.ConnectionDiagnosticsScreen
import com.example.sophieaianalyst.ui.screens.home.HomeScreen
import com.example.sophieaianalyst.ui.screens.home.HomeViewModel

/**
 * Navigation destinations for the app
 */
object SophieDestinations {
    const val HOME_ROUTE = "home"
    const val STOCK_DETAILS_ROUTE = "stock"
    const val STOCK_DETAILS_ARG = "ticker"
    const val STOCK_DETAILS_FULL_ROUTE = "$STOCK_DETAILS_ROUTE/{$STOCK_DETAILS_ARG}"
    const val DIAGNOSTICS_ROUTE = "diagnostics"
    
    fun stockDetailsRoute(ticker: String) = "$STOCK_DETAILS_ROUTE/$ticker"
}

/**
 * Navigation graph for the app
 */
@Composable
fun SophieNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // Create shared ViewModel using the Factory
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    
    NavHost(
        navController = navController,
        startDestination = SophieDestinations.HOME_ROUTE,
        modifier = modifier
    ) {
        // Home screen
        composable(SophieDestinations.HOME_ROUTE) {
            HomeScreen(
                viewModel = homeViewModel,
                onStockClick = { ticker ->
                    navController.navigate(SophieDestinations.stockDetailsRoute(ticker))
                },
                onDiagnosticsClick = {
                    navController.navigate(SophieDestinations.DIAGNOSTICS_ROUTE)
                }
            )
        }
        
        // Stock details screen
        composable(
            route = SophieDestinations.STOCK_DETAILS_FULL_ROUTE,
            arguments = listOf(
                navArgument(SophieDestinations.STOCK_DETAILS_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            // Extract ticker from arguments
            val ticker = backStackEntry.arguments?.getString(SophieDestinations.STOCK_DETAILS_ARG) ?: ""
            
            DetailsScreen(
                ticker = ticker,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Diagnostics screen
        composable(SophieDestinations.DIAGNOSTICS_ROUTE) {
            ConnectionDiagnosticsScreen(
                homeViewModel = homeViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
} 