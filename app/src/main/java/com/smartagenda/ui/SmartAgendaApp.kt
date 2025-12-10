package com.smartagenda.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartagenda.ui.screens.HomeScreen
import com.smartagenda.ui.screens.SetupScreen
import com.smartagenda.ui.viewmodel.MainViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Routes de navigation
 */
sealed class Screen(val route: String) {
    object Setup : Screen("setup")
    object Home : Screen("home")
}

/**
 * Point d'entrée principal de l'application
 */
@Composable
fun SmartAgendaApp() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    
    // Déterminer la destination initiale UNE SEULE FOIS
    val scope = rememberCoroutineScope()
    val startDestination = remember {
        var destination = Screen.Setup.route
        scope.launch {
            val isConfigured = mainViewModel.isConfigured.first()
            destination = if (isConfigured) Screen.Home.route else Screen.Setup.route
        }
        destination
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Setup.route) {
            SetupScreen(
                onSetupComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Setup.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}
