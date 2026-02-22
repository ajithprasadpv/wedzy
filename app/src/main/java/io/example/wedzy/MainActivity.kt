package io.example.wedzy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.example.wedzy.data.local.PreferencesDataStore
import io.example.wedzy.data.model.Currency
import io.example.wedzy.ui.components.WedzyBottomBar
import io.example.wedzy.ui.navigation.Screen
import io.example.wedzy.ui.navigation.WedzyNavHost
import io.example.wedzy.ui.theme.WedzyTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WedzyTheme {
                WedzyApp()
            }
        }
    }
}

@Composable
fun WedzyApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    
    val preferencesDataStore = PreferencesDataStore(context)
    val currencyCode by preferencesDataStore.selectedCurrency.collectAsState(initial = "USD")
    val currencySymbol = Currency.fromCode(currencyCode).symbol
    
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Tasks.route,
        Screen.Budget.route,
        Screen.Guests.route,
        Screen.Calendar.route,
        Screen.Vendors.route
    )
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                WedzyBottomBar(
                    navController = navController,
                    currencySymbol = currencySymbol
                )
            }
        }
    ) { innerPadding ->
        WedzyNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}