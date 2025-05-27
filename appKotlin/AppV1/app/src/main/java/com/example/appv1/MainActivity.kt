package com.example.appv1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appv1.ui.screens.home.HomeScreen
import com.example.appv1.ui.screens.list.ListReportsScreen
import com.example.appv1.ui.screens.report.ConfirmationScreen
import com.example.appv1.ui.screens.report.NewReportScreen
import com.example.appv1.ui.screens.report.ReportSharedViewModel
import com.example.appv1.ui.theme.AppV1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppV1Theme(dynamicColor = false) {
                val navController = rememberNavController()
                val sharedViewModel: ReportSharedViewModel = viewModel()
                NavHost(navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            onNewReport = { navController.navigate("new_report") },
                            onViewReports = { navController.navigate("list_reports") }
                        )
                    }
                    composable(
                        route = "new_report?showSuccess={showSuccess}",
                        arguments = listOf(navArgument("showSuccess") { type = NavType.BoolType; defaultValue = false })
                    ) { backStackEntry ->
                        val showSuccess = backStackEntry.arguments?.getBoolean("showSuccess") ?: false
                        NewReportScreen(
                            onBack = { navController.popBackStack() },
                            onSuccess = { response, trainType, trainCar, seatNumber ->
                                sharedViewModel.lastIncidentAnalysisResponse = response
                                sharedViewModel.trainType = trainType
                                sharedViewModel.trainCar = trainCar
                                sharedViewModel.seatNumber = seatNumber
                                navController.navigate("confirm_report")
                            },
                            showSuccessMessage = showSuccess
                        )
                    }
                    composable("confirm_report") {
                        ConfirmationScreen(
                            response = sharedViewModel.lastIncidentAnalysisResponse,
                            onBack = { navController.popBackStack() },
                            trainType = sharedViewModel.trainType ?: "", // Fournir une valeur par défaut si null
                            trainCar = sharedViewModel.trainCar ?: "",   // Fournir une valeur par défaut si null
                            seatNumber = sharedViewModel.seatNumber,
                            navigateToNewReport = {
                                navController.navigate("new_report?showSuccess=true") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable("list_reports") {
                        ListReportsScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
