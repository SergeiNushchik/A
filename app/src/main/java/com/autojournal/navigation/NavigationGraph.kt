package com.autojournal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.autojournal.data.preferences.SettingsPreferences
import com.autojournal.ui.screens.*
import com.autojournal.ui.viewmodels.DashboardViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: DashboardViewModel = hiltViewModel(),
    openRefuel: Boolean = false
) {
    val selectedCarId by viewModel.selectedCarId.collectAsState()

    // Обработка Intent из виджета
    LaunchedEffect(openRefuel) {
        if (openRefuel) {
            if (!selectedCarId.isNullOrEmpty()) {
                navController.navigate("add_refueling/$selectedCarId") {
                    popUpTo("main_menu") { inclusive = false }
                }
            } else {
                navController.navigate("garage") {
                    popUpTo("main_menu") { inclusive = false }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "main_menu"
    ) {
        // ===== ГЛАВНОЕ МЕНЮ =====
        composable("main_menu") {
            MainMenuScreen(
                onNavigate = { route ->
                    when (route) {
                        "garage" -> navController.navigate("garage")
                        "service" -> {
                            if (!selectedCarId.isNullOrEmpty()) {
                                navController.navigate("history/$selectedCarId")
                            } else {
                                navController.navigate("garage")
                            }
                        }
                        "repairs" -> {
                            if (!selectedCarId.isNullOrEmpty()) {
                                navController.navigate("repairs/$selectedCarId")
                            } else {
                                navController.navigate("garage")
                            }
                        }
                        "refueling" -> navController.navigate("refueling")
                        "obd" -> navController.navigate("obd")
                        "statistics" -> {
                            if (!selectedCarId.isNullOrEmpty()) {
                                navController.navigate("statistics/$selectedCarId")
                            } else {
                                navController.navigate("garage")
                            }
                        }
                        "settings" -> navController.navigate("settings")
                    }
                },
                viewModel = viewModel
            )
        }

        // ===== ГАРАЖ =====
        composable("garage") {
            GarageScreen(
                viewModel = viewModel,
                onCarSelect = { carId ->
                    viewModel.selectCar(carId)
                    navController.navigate("car_info/$carId")
                },
                onAddCar = { navController.navigate("add_car") },
                onEditCar = { carId -> navController.navigate("edit_car/$carId") },
                onBack = { navController.navigateUp() }
            )
        }

        // ===== ДОБАВЛЕНИЕ АВТО =====
        composable("add_car") {
            AddCarScreen(
                viewModel = viewModel,
                onBack = { navController.navigateUp() },
                onCarAdded = { carId ->
                    viewModel.selectCar(carId)
                    navController.navigate("dashboard")
                }
            )
        }

        // ===== РЕДАКТИРОВАНИЕ АВТО =====
        composable(
            route = "edit_car/{carId}",
            arguments = listOf(navArgument("carId") { defaultValue = "" })
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: ""
            AddCarScreen(
                viewModel = viewModel,
                editCarId = carId,
                onBack = { navController.navigateUp() },
                onCarAdded = { }
            )
        }

        // ===== ДАШБОРД =====
        composable("dashboard") {
            if (!selectedCarId.isNullOrEmpty()) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToOBD = { navController.navigate("obd") },
                    onNavigateToSettings = { navController.navigate("settings") },
                    onNavigateToAddCar = { navController.navigate("add_car") }
                )
            } else {
                navController.navigate("garage") {
                    popUpTo("main_menu") { inclusive = false }
                }
            }
        }

        // ===== ИНФОРМАЦИЯ ОБ АВТОМОБИЛЕ =====
        composable(
            route = "car_info/{carId}",
            arguments = listOf(navArgument("carId") { defaultValue = "" })
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: ""

            androidx.compose.runtime.key(carId) {
                val car = viewModel.cars.value.find { it.id == carId }

                if (car != null) {
                    CarInfoScreen(
                        car = car,
                        viewModel = viewModel,
                        onBack = {
                            navController.popBackStack()
                            viewModel.selectCar(carId)
                        },
                        onAddExpense = {
                            navController.navigate("add_expense/$carId")
                        },
                        onAddRefueling = {
                            navController.navigate("add_refueling/$carId")
                        },
                        onNavigateToHistory = {
                            navController.navigate("history/$carId")
                        },
                        onNavigateToService = {
                            navController.navigate("repairs/$carId")
                        },
                        onNavigateToStatistics = {
                            navController.navigate("statistics/$carId")
                        }
                    )
                } else {
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }
        }

        // ===== ИСТОРИЯ =====
        composable(
            route = "history/{carId}",
            arguments = listOf(navArgument("carId") { defaultValue = "" })
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: ""
            HistoryScreen(
                carId = carId,
                viewModel = viewModel,
                onBack = { navController.navigateUp() },
                onAddExpense = { navController.navigate("add_expense/$carId") }
            )
        }

        // ===== РЕМОНТЫ =====
        composable(
            route = "repairs/{carId}",
            arguments = listOf(navArgument("carId") { defaultValue = "" })
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: ""
            RepairsScreen(
                carId = carId,
                viewModel = viewModel,
                onBack = { navController.navigateUp() },
                onAddRepair = { navController.navigate("add_repair/$carId") },
                onNavigateToMap = { taskTitle ->
                    navController.navigate("shop_map/$taskTitle")
                }
            )
        }

        // ===== ЗАПРАВКИ =====
        composable("refueling") {
            RefuelingScreen(
                viewModel = viewModel,
                onAddRefueling = { carId ->
                    navController.navigate("add_refueling/$carId")
                },
                onBack = { navController.navigateUp() }
            )
        }

        // ===== СТАТИСТИКА =====
        composable(
            route = "statistics/{carId}",
            arguments = listOf(navArgument("carId") { defaultValue = "" })
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: ""
            StatisticsScreen(
                carId = carId,
                viewModel = viewModel,
                onBack = { navController.navigateUp() }
            )
        }

        // ===== НАСТРОЙКИ =====
        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.navigateUp() }
            )
        }

        // ===== OBD =====
        composable("obd") {
            OBDScreen(
                onBack = { navController.navigateUp() },
                onNavigateToSettings = { navController.navigate("obd_settings") }
            )
        }

        // ===== НАСТРОЙКИ OBD =====
        composable("obd_settings") {
            val context = LocalContext.current
            val prefs = remember { SettingsPreferences(context) }
            ObdSettingsScreen(
                prefs = prefs,
                onBack = { navController.navigateUp() }
            )
        }

        // ===== ДОБАВЛЕНИЕ ТРАТ =====
        composable(
            route = "add_expense/{carId}",
            arguments = listOf(navArgument("carId") { defaultValue = "" })
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: ""
            AddExpenseScreen(
                carId = carId,
                viewModel = viewModel,
                onBack = { navController.navigateUp() },
                onSave = { expense ->
                    viewModel.addExpense(expense)
                    navController.navigateUp()
                }
            )
        }

        // ===== ДОБАВЛЕНИЕ ЗАПРАВКИ =====
        composable(
            route = "add_refueling/{carId}",
            arguments = listOf(navArgument("carId") { defaultValue = "" })
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: ""
            AddRefuelingScreen(
                carId = carId,
                viewModel = viewModel,
                onBack = { navController.navigateUp() },
                onSave = { expense ->
                    viewModel.addExpense(expense)
                    navController.navigateUp()
                }
            )
        }

        // ===== ДОБАВЛЕНИЕ РЕМОНТА =====
        composable(
            route = "add_repair/{carId}",
            arguments = listOf(navArgument("carId") { defaultValue = "" })
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: ""
            AddRepairScreen(
                carId = carId,
                viewModel = viewModel,
                onBack = { navController.navigateUp() },
                onSave = { task ->
                    viewModel.addTask(task)
                    navController.navigateUp()
                }
            )
        }

        // ===== КАРТА СТО =====
        composable(
            route = "shop_map/{taskTitle}",
            arguments = listOf(navArgument("taskTitle") { defaultValue = "" })
        ) { backStackEntry ->
            val taskTitle = backStackEntry.arguments?.getString("taskTitle") ?: ""
            ShopMapScreen(
                taskTitle = taskTitle,
                onBack = { navController.navigateUp() }
            )
        }
    }
}