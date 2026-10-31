package com.example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.widget.Toast

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val viewModel: DashboardViewModel = viewModel()
    val context = LocalContext.current
    
    // A persisted Firebase user goes straight to the dashboard instead of
    // being asked to sign in again on every cold start.
    NavHost(navController = navController, startDestination = if (AuthManager.isUserSignedIn()) "dashboard" else "auth") {
        composable("auth") {
            AuthScreen(
                onSignInClick = {
                    viewModel.signIn(context, onSuccess = {
                        navController.navigate("dashboard") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }, onError = { errorMsg ->
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    })
                }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToLogs = { navController.navigate("logs") },
                onNavigateToPromptManager = { navController.navigate("prompt_manager") }
            )
        }
        composable("settings") {
            ApiSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("logs") {
            LogHistoryScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToStats = { navController.navigate("stats") }
            )
        }
        composable("prompt_manager") {
            PromptManagerScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onPromptSelected = { prompt ->
                    viewModel.setInputText(prompt.content)
                    viewModel.setActiveModel(prompt.targetModel)
                }
            )
        }
        composable("stats") {
            VisualStatsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
