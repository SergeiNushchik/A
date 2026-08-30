package com.autojournal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.autojournal.data.ExpenseCategories
import com.autojournal.ui.components.InfoChip
import com.autojournal.ui.viewmodels.DashboardViewModel
import com.autojournal.utils.formatMoney

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    carId: String,
    viewModel: DashboardViewModel,
    onBack: () -> Unit
) {
    val car = viewModel.getSelectedCar()
    val expenses by viewModel.expenses.collectAsState()

    // Фильтруем расходы только для текущего авто
    val carExpenses = remember(expenses, carId) {
        expenses.filter { it.carId == carId }
    }

    val totalExpenses = carExpenses.sumOf { it.amount }

    // ===== КОРРЕКТНАЯ РАЗБИВКА ПО КАТЕГОРИЯМ =====
    val categoryBreakdown = remember(carExpenses) {
        val map = mutableMapOf<String, Double>()
        for (expense in carExpenses) {
            // Извлекаем корневую категорию из строки вида "🔧 Двигатель → ..." или "⛽ Заправка (...)"
            val rootCategory = extractRootCategory(expense.category)
            map[rootCategory] = (map[rootCategory] ?: 0.0) + expense.amount
        }
        map.toSortedMap()
    }

    // Вычисляем пробег
    val totalDistance = if (carExpenses.isNotEmpty()) {
        val mileages = carExpenses.mapNotNull { 
            if (it.mileage > 0) it.mileage else null 
        }
        if (mileages.size >= 2) {
            mileages.max() - mileages.min()
        } else 0
    } else 0

    val costPerKm = if (totalDistance > 0) totalExpenses / totalDistance else 0.0

    // Расход топлива (грубая оценка по заправкам)
    val fuelAmount = categoryBreakdown.entries
        .filter { it.key.contains("Заправка") || it.key.contains("Топливо") }
        .sumOf { it.value }
    val fuelLiters = fuelAmount / 2.70 // средняя цена АИ-95
    val fuelConsumption = if (totalDistance > 0) (fuelLiters / totalDistance) * 100 else 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 Финансовая статистика") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                StatsOverview(
                    totalExpenses = totalExpenses,
                    costPerKm = costPerKm,
                    fuelConsumption = fuelConsumption
                )
            }

            // ===== ДИНАМИЧЕСКАЯ РАЗБИВКА ПО КАТЕГОРИЯМ =====
            item {
                DynamicCategoryBreakdown(categoryBreakdown = categoryBreakdown)
            }

            if (totalDistance > 0) {
                item {
                    FuelStats(
                        fuelConsumption = fuelConsumption,
                        totalDistance = totalDistance,
                        fuelLiters = fuelLiters
                    )
                }
            }

            item {
                RecordsCount(
                    totalRecords = carExpenses.size,
                    categoryCounts = categoryBreakdown.mapValues { (_, v) ->
                        carExpenses.count { extractRootCategory(it.category) == extractRootCategory(it.category) && v > 0 }
                    }
                )
            }

            item { CarInfo(car = car) }

            if (carExpenses.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            text = "📭 Нет данных для отображения статистики",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

// ===== ИЗВЛЕЧЕНИЕ КОРНЕВОЙ КАТЕГОРИИ =====
private fun extractRootCategory(categoryString: String): String {
    // Формат: "🔧 Двигатель → Подкатегория → Позиция" или "⛽ Заправка (АИ-95)"
    val arrowIndex = categoryString.indexOf("→")
    val parenIndex = categoryString.indexOf("(")

    return when {
        arrowIndex > 0 -> categoryString.substring(0, arrowIndex).trim()
        parenIndex > 0 -> categoryString.substring(0, parenIndex).trim()
        else -> categoryString.trim()
    }
}

// ===== ЦВЕТА ДЛЯ КАТЕГОРИЙ =====
private fun getCategoryColor(category: String): Color {
    return when {
        category.contains("Двигатель") -> Color(0xFFF44336)
        category.contains("Ходовая") -> Color(0xFFFF9800)
        category.contains("Электрика") -> Color(0xFFFFEB3B)
        category.contains("Охлаждение") -> Color(0xFF2196F3)
        category.contains("Трансмиссия") -> Color(0xFF9C27B0)
        category.contains("Кузов") -> Color(0xFF795548)
        category.contains("Заправка") || category.contains("Топливо") -> Color(0xFFFF9800)
        category.contains("ТО") || category.contains("Расходники") -> Color(0xFF607D8B)
        category.contains("Прочее") -> Color(0xFF9E9E9E)
        else -> Color(0xFFB0BEC5)
    }
}

@Composable
fun DynamicCategoryBreakdown(categoryBreakdown: Map<String, Double>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📊 Траты по категориям",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (categoryBreakdown.isEmpty()) {
                Text(
                    text = "Нет данных",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                // Сортируем по убыванию суммы
                categoryBreakdown.entries
                    .sortedByDescending { it.value }
                    .forEach { (category, amount) ->
                        CategoryRow(
                            label = category,
                            amount = amount,
                            color = getCategoryColor(category)
                        )
                    }
            }
        }
    }
}

@Composable
fun StatsOverview(
    totalExpenses: Double,
    costPerKm: Double,
    fuelConsumption: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📈 Основные показатели",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoChip("Всего трат", formatMoney(totalExpenses))
                InfoChip("1 км", formatMoney(costPerKm))
                InfoChip("Расход", "${String.format("%.1f", fuelConsumption)} л/100км")
            }
        }
    }
}

@Composable
fun CategoryRow(label: String, amount: Double, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            formatMoney(amount),
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun FuelStats(
    fuelConsumption: Double,
    totalDistance: Int,
    fuelLiters: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "⛽ Расход топлива",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoChip("Средний расход", "${String.format("%.1f", fuelConsumption)} л/100км")
                InfoChip("Всего литров", "${String.format("%.0f", fuelLiters)} л")
                InfoChip("Пробег", "${totalDistance} км")
            }
        }
    }
}

@Composable
fun RecordsCount(
    totalRecords: Int,
    categoryCounts: Map<String, Int>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📋 Количество записей",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoChip("Всего", "$totalRecords")
                // Показываем топ-3 категории по количеству
                categoryCounts.entries
                    .sortedByDescending { it.value }
                    .take(3)
                    .forEach { (cat, count) ->
                        InfoChip(cat.take(10), "$count")
                    }
            }
        }
    }
}

@Composable
fun CarInfo(car: com.autojournal.data.model.Car?) {
    if (car == null) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🚗 Информация об автомобиле",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoChip("Марка", car.brand)
                InfoChip("Модель", car.model)
                InfoChip("Год", car.year.toString())
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoChip("Пробег", "${car.mileage} км")
                InfoChip("Госномер", car.plate)
            }
        }
    }
}