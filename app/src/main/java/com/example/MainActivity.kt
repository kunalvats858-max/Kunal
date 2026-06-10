package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ConvictionViewModel
import com.example.ui.screens.CoachScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.NutritionScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.WorkoutsScreen
import com.example.ui.theme.ConvictionTheme
import com.example.ui.theme.ConvictionGold
import com.example.ui.theme.ConvictionPrimaryRed

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConvictionTheme {
                MainAppLayout()
            }
        }
    }
}

enum class ConvictionTab {
    Dashboard, Combat, Nutrition, Coach, Resolve
}

@Composable
fun MainAppLayout() {
    val viewModel: ConvictionViewModel = viewModel()
    var currentTab by remember { mutableStateOf(ConvictionTab.Dashboard) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("app_navigation_bar")
            ) {
                // Dashboard Tab
                NavigationBarItem(
                    selected = currentTab == ConvictionTab.Dashboard,
                    onClick = { currentTab = ConvictionTab.Dashboard },
                    label = { Text("Dashboard", style = MaterialTheme.typography.labelSmall) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = "Dashboard",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ConvictionPrimaryRed,
                        selectedTextColor = ConvictionPrimaryRed,
                        unselectedIconColor = Color.White.copy(alpha = 0.4f),
                        unselectedTextColor = Color.White.copy(alpha = 0.4f),
                        indicatorColor = ConvictionPrimaryRed.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.testTag("tab_dashboard")
                )

                // Combat / Workouts Tab
                NavigationBarItem(
                    selected = currentTab == ConvictionTab.Combat,
                    onClick = { currentTab = ConvictionTab.Combat },
                    label = { Text("Combat", style = MaterialTheme.typography.labelSmall) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = "Combat",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ConvictionPrimaryRed,
                        selectedTextColor = ConvictionPrimaryRed,
                        unselectedIconColor = Color.White.copy(alpha = 0.4f),
                        unselectedTextColor = Color.White.copy(alpha = 0.4f),
                        indicatorColor = ConvictionPrimaryRed.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.testTag("tab_combat")
                )

                // Nutrition Cam Tab
                NavigationBarItem(
                    selected = currentTab == ConvictionTab.Nutrition,
                    onClick = { currentTab = ConvictionTab.Nutrition },
                    label = { Text("Nutrition", style = MaterialTheme.typography.labelSmall) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Nutrition Scan",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ConvictionPrimaryRed,
                        selectedTextColor = ConvictionPrimaryRed,
                        unselectedIconColor = Color.White.copy(alpha = 0.4f),
                        unselectedTextColor = Color.White.copy(alpha = 0.4f),
                        indicatorColor = ConvictionPrimaryRed.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.testTag("tab_nutrition")
                )

                // AI Coach Tab
                NavigationBarItem(
                    selected = currentTab == ConvictionTab.Coach,
                    onClick = { currentTab = ConvictionTab.Coach },
                    label = { Text("Coach", style = MaterialTheme.typography.labelSmall) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Coach AI",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ConvictionGold,
                        selectedTextColor = ConvictionGold,
                        unselectedIconColor = Color.White.copy(alpha = 0.4f),
                        unselectedTextColor = Color.White.copy(alpha = 0.4f),
                        indicatorColor = ConvictionGold.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.testTag("tab_coach")
                )

                // Resolve Profile Tab
                NavigationBarItem(
                    selected = currentTab == ConvictionTab.Resolve,
                    onClick = { currentTab = ConvictionTab.Resolve },
                    label = { Text("Resolve", style = MaterialTheme.typography.labelSmall) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile Builder",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ConvictionPrimaryRed,
                        selectedTextColor = ConvictionPrimaryRed,
                        unselectedIconColor = Color.White.copy(alpha = 0.4f),
                        unselectedTextColor = Color.White.copy(alpha = 0.4f),
                        indicatorColor = ConvictionPrimaryRed.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.testTag("tab_resolve")
                )
            }
        }
    ) { innerPadding ->
        when (currentTab) {
            ConvictionTab.Dashboard -> DashboardScreen(viewModel = viewModel, innerPadding = innerPadding)
            ConvictionTab.Combat -> WorkoutsScreen(viewModel = viewModel, innerPadding = innerPadding)
            ConvictionTab.Nutrition -> NutritionScreen(viewModel = viewModel, innerPadding = innerPadding)
            ConvictionTab.Coach -> CoachScreen(viewModel = viewModel, innerPadding = innerPadding)
            ConvictionTab.Resolve -> ProfileScreen(viewModel = viewModel, innerPadding = innerPadding)
        }
    }
}
