package com.example.appv1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appv1.ui.screens.home.HomeScreen
import com.example.appv1.ui.screens.NewReportScreen
import com.example.appv1.ui.theme.AppV1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppV1Theme(dynamicColor = false) {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            onNewReport = { navController.navigate("new_report") },
                            onViewReports = { /* TODO: lister les rapports */ }
                        )
                    }
                    composable("new_report") {
                        NewReportScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
