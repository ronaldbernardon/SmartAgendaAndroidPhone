package com.smartagenda.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartagenda.ui.screens.HomeScreen
import com.smartagenda.ui.screens.SetupScreen
import com.smartagenda.ui.viewmodel.MainViewModel

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
    val isConfigured by mainViewModel.isConfigured.collectAsState(initial = false)

    NavHost(
        navController = navController,
        startDestination = if (isConfigured) Screen.Home.route else Screen.Setup.route
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
