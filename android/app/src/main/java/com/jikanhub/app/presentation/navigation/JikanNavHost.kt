package com.jikanhub.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jikanhub.app.presentation.screens.dashboard.DashboardScreen
import com.jikanhub.app.presentation.screens.auth.LoginScreen
import com.jikanhub.app.presentation.screens.auth.RegisterScreen

@Composable
fun JikanNavHost(
    startDestination: String = "login"
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                },
                onNavigateToStats = {
                    navController.navigate("stats")
                }
            )
        }

        composable("settings") {
            com.jikanhub.app.presentation.screens.settings.SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("stats") {
            com.jikanhub.app.presentation.screens.stats.StatsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
