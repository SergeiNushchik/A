package com.autojournal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.autojournal.data.model.Car
import com.autojournal.ui.components.CarSelector
import com.autojournal.ui.components.InfoChip
import com.autojournal.ui.components.RepairTaskCard
import com.autojournal.ui.viewmodels.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToOBD: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAddCar: () -> Unit
) {
    val cars by viewModel.cars.collectAsState()
    val selectedCarId by viewModel.selectedCarId.collectAsState()
    val pendingTasks by viewModel.pendingTasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val selectedCar = cars.find { it.id == selectedCarId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🚗 АвтоЖурнал") },
                actions = {
                    IconButton(onClick = onNavigateToOBD) {
                        Icon(Icons.Default.Bluetooth, contentDescription = "OBD")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Настройки")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (cars.isNotEmpty()) {
                item {
                    CarSelector(
                        cars = cars,
                        selectedCar = selectedCar,
                        onCarSelected = { carId ->
                            viewModel.selectCar(carId)
                        }
                    )
                }

                item {
                    HealthCard(
                        car = selectedCar,
                        obdConnected = false,
                        dtcCount = 0
                    )
                }

                item {
                    FuelCard(
                        fuelConsumption = 8.5,
                        mileage = selectedCar?.mileage ?: 0
                    )
                }

                if (pendingTasks.isNotEmpty()) {
                    item {
                        Text(
                            text = "🔧 Предстоящий ремонт (${pendingTasks.size})",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    items(pendingTasks.take(3)) { task ->
                        RepairTaskCard(
                            task = task,
                            onComplete = {
                                viewModel.completeTask(task.id)
                                viewModel.loadData()
                            },
                            onDelete = {
                                viewModel.deleteTask(task)
                                viewModel.loadData()
                            },
                            onShowOnMap = {
                                // Здесь можно добавить навигацию на карту
                                // Например: navController.navigate("shop_map/${task.title}")
                            }
                        )
                    }
                }
            } else {
                item {
                    EmptyState(onAddCar = onNavigateToAddCar)
                }
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun HealthCard(
    car: Car?,
    obdConnected: Boolean,
    dtcCount: Int,
    modifier: Modifier = Modifier
) {
    if (car == null) return

    val healthColor = when {
        dtcCount > 0 -> Color(0xFFD32F2F)
        car.mileage - car.lastOilChangeKm > 12000 -> Color(0xFFF9A825)
        else -> Color(0xFF388E3C)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = healthColor.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🚗 ${car.brand} ${car.model}",
                    style = MaterialTheme.typography.titleMedium
                )
                if (obdConnected) {
                    Text("🟢 Online", color = Color.Green)
                } else {
                    Text("⚪ Offline", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoChip("Пробег", "${car.mileage} км")
                InfoChip("ТО", "${15000 - (car.mileage - car.lastOilChangeKm)} км")
                InfoChip("Ошибки", "$dtcCount")
            }
        }
    }
}

@Composable
fun FuelCard(
    fuelConsumption: Double,
    mileage: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⛽", style = MaterialTheme.typography.titleLarge)
                Text(
                    "${String.format("%.1f", fuelConsumption)}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("л/100км", style = MaterialTheme.typography.bodySmall)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📊", style = MaterialTheme.typography.titleLarge)
                Text(
                    "${String.format("%.2f", fuelConsumption * 2.5)}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("BYN/100км", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun EmptyState(onAddCar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.DirectionsCar,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Добавьте свой первый автомобиль",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Записывайте траты, диагностируйте авто и планируйте ремонт",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAddCar) {
            Text("➕ Добавить авто")
        }
    }
}