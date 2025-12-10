package com.smartagenda.ui

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartagenda.data.local.PreferencesManager
import com.smartagenda.ui.screens.HomeScreen
import com.smartagenda.ui.screens.SetupScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Setup : Screen("setup")
    object Home : Screen("home")
}

@Composable
fun SmartAgendaApp(
    preferencesManager: PreferencesManager
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    
    // Déterminer la destination initiale UNE FOIS au démarrage
    var startDestination by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        val isConfigured = preferencesManager.isConfiguredFlow.first()
        startDestination = if (isConfigured) Screen.Home.route else Screen.Setup.route
    }
    
    // Attendre que la destination soit déterminée
    if (startDestination == null) {
        // Écran de chargement minimal pendant l'initialisation
        return
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination!!
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
