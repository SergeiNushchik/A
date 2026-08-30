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
import com.autojournal.ui.components.RepairTaskCard
import com.autojournal.ui.theme.ThemeManager
import com.autojournal.ui.viewmodels.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairsScreen(
    carId: String,
    viewModel: DashboardViewModel,
    onBack: () -> Unit,
    onAddRepair: (String) -> Unit,
    onNavigateToMap: (String) -> Unit  // Новая функция для карты
) {
    val tasks by viewModel.pendingTasks.collectAsState()
    val cars by viewModel.cars.collectAsState()

    // Фильтруем задачи для текущего автомобиля
    val filteredTasks = tasks.filter { it.carId == carId }
    val currentCar = cars.find { it.id == carId }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "📋 Планер ремонта",
                        color = ThemeManager.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = ThemeManager.accentColor
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onAddRepair(carId) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить ремонт",
                            tint = ThemeManager.accentColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Информация об автомобиле
            if (currentCar != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E)
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "🚗 ${currentCar.brand} ${currentCar.model}",
                            color = ThemeManager.textPrimary,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "📊 ${currentCar.mileage} км",
                            color = ThemeManager.textSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Список задач
            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = ThemeManager.textHint
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Нет задач на ремонт",
                            style = MaterialTheme.typography.titleMedium,
                            color = ThemeManager.textPrimary
                        )
                        Text(
                            text = "Добавьте новую задачу",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ThemeManager.textSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredTasks) { task ->
                        RepairTaskCard(
                            task = task,
                            onComplete = {
                                // Завершаем ремонт
                                viewModel.completeTask(task.id)
                            },
                            onDelete = {
                                viewModel.deleteTask(task)
                            },
                            onShowOnMap = {
                                // Переход к карте
                                onNavigateToMap(task.title)
                            }
                        )
                    }
                }
            }
        }
    }
}