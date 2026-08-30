package com.autojournal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.autojournal.data.model.Car
import com.autojournal.data.preferences.SettingsPreferences
import com.autojournal.ui.theme.ThemeManager
import com.autojournal.ui.viewmodels.DashboardViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarInfoScreen(
    car: Car,
    viewModel: DashboardViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onAddExpense: () -> Unit,
    onAddRefueling: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToService: () -> Unit,
    onNavigateToStatistics: () -> Unit
) {
    // ✅ ПРИНУДИТЕЛЬНО ОБНОВЛЯЕМ КОМПОЗИЦИЮ ПРИ ИЗМЕНЕНИИ ID АВТО
    val carId = car.id

    // ✅ ПОДПИСЫВАЕМСЯ НА ОБНОВЛЕНИЯ СПИСКА АВТО
    val cars by viewModel.cars.collectAsState()

    // ✅ НАХОДИМ АКТУАЛЬНЫЙ АВТО ПО ID
    val currentCar = cars.find { it.id == carId } ?: car
    val context = LocalContext.current
    val prefs = remember { SettingsPreferences(context) }
    val expenses by viewModel.expenses.collectAsState()

    // Фильтруем расходы для этого авто
    val carExpenses = expenses.filter { it.carId == car.id }
    val totalExpenses = carExpenses.sumOf { it.amount }
    val fuelExpenses = carExpenses.filter {
        it.category.contains("Топливо") || it.category.contains("Заправка")
    }
    val totalFuelCost = fuelExpenses.sumOf { it.amount }
    val avgFuelConsumption = if (fuelExpenses.isNotEmpty()) {
        val totalLiters = fuelExpenses.sumOf { it.amount / (prefs.getFuelPrice() ?: 2.5f) }
        val totalDistance = car.mileage
        if (totalDistance > 0) {
            (totalLiters / totalDistance * 100)
        } else 0.0
    } else 0.0

    Scaffold(
        containerColor = Color(0xFF0A0A0A),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${car.brand} ${car.model}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToStatistics) {
                        Icon(
                            Icons.Default.BarChart,
                            contentDescription = "Статистика",
                            tint = Color.White
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ===== ФОТО АВТОМОБИЛЯ =====
            CarPhotoSection(car = car)

            // ===== ОСНОВНАЯ ИНФОРМАЦИЯ =====
            CarMainInfoCard(car = car)

            // ===== СТАТИСТИКА =====
            CarStatisticsRow(
                fuelConsumption = avgFuelConsumption,
                totalExpenses = totalExpenses,
                totalFuelCost = totalFuelCost
            )

            // ===== БЫСТРЫЕ ДЕЙСТВИЯ =====
            QuickActionsGrid(
                onAddExpense = onAddExpense,
                onAddRefueling = onAddRefueling,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToService = onNavigateToService
            )

            // ===== ПРЕДСТОЯЩИЕ СОБЫТИЯ =====
            UpcomingEvents(car = car)
        }
    }
}

@Composable
fun CarPhotoSection(car: Car) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Фото автомобиля
            if (!car.photoUrl.isNullOrEmpty()) {
                val photoFile = File(car.photoUrl)
                if (photoFile.exists()) {
                    AsyncImage(
                        model = photoFile,
                        contentDescription = "${car.brand} ${car.model}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Если файл не найден - показываем заглушку
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF2A2A2A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.DirectionsCar,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF555555)
                        )
                    }
                }
                // Градиентное затемнение
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                ),
                                startY = 0.5f,
                                endY = 1f
                            )
                        )
                )
            } else {
                // Заглушка
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF2A2A2A)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.DirectionsCar,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF555555)
                    )
                }
            }

            // Информация поверх фото
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "${car.brand} ${car.model}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                        Text(
                            text = car.plate,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${car.year} год",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }

                    // Статус
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50))
                        )
                        Text(
                            text = "Online",
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CarMainInfoCard(car: Car) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CarInfoItem(
                label = "Пробег",
                value = "${car.mileage} км",
                icon = "📊"
            )

            Divider(
                color = Color(0xFF333333),
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp)
            )

            CarInfoItem(
                label = "ТО",
                value = "${car.lastOilChangeKm} км",
                icon = "🔧"
            )

            Divider(
                color = Color(0xFF333333),
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp)
            )

            CarInfoItem(
                label = "Топливо",
                value = car.fuelType,
                icon = "⛽"
            )
        }
    }
}

@Composable
fun CarInfoItem(
    label: String,
    value: String,
    icon: String,
    valueColor: Color = Color.White
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = valueColor,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 11.sp
        )
    }
}

@Composable
fun CarStatisticsRow(
    fuelConsumption: Double,
    totalExpenses: Double,
    totalFuelCost: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItemWithIcon(
                icon = "⛽",
                label = String.format("%.1f", fuelConsumption),
                value = "л/100км"
            )

            Divider(
                color = Color(0xFF333333),
                modifier = Modifier
                    .height(30.dp)
                    .width(1.dp)
            )

            StatItemWithIcon(
                icon = "💰",
                label = "${String.format("%.0f", totalExpenses)} ₽",
                value = "Всего расходов"
            )

            Divider(
                color = Color(0xFF333333),
                modifier = Modifier
                    .height(30.dp)
                    .width(1.dp)
            )

            StatItemWithIcon(
                icon = "⛽",
                label = "${String.format("%.0f", totalFuelCost)} ₽",
                value = "На топливо"
            )
        }
    }
}

@Composable
fun StatItemWithIcon(
    icon: String,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = 20.sp)
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        if (value.isNotEmpty()) {
            Text(
                text = value,
                color = Color.Gray,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun QuickActionsGrid(
    onAddExpense: () -> Unit,
    onAddRefueling: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToService: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Быстрые действия",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                icon = "💰",
                label = "Расходы",
                onClick = onAddExpense,
                modifier = Modifier.weight(1f)
            )

            QuickActionButton(
                icon = "⛽",
                label = "Заправка",
                onClick = onAddRefueling,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                icon = "📜",
                label = "История",
                onClick = onNavigateToHistory,
                modifier = Modifier.weight(1f)
            )

            QuickActionButton(
                icon = "🔧",
                label = "Сервис",
                onClick = onNavigateToService,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickActionButton(
    icon: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(72.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun UpcomingEvents(car: Car) {
    // Проверяем, нужно ли напоминание о ТО
    val serviceDue = car.mileage - car.lastOilChangeKm > 5000
    val daysUntilService = if (serviceDue) {
        ((car.mileage - car.lastOilChangeKm - 5000) / 100) * 30 // Примерный расчет
    } else 0

    if (serviceDue) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFF6F00).copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚠️",
                        fontSize = 24.sp
                    )
                    Column {
                        Text(
                            text = "Предстоящее ТО!",
                            color = Color(0xFFFF6F00),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Пробег ${car.mileage} км, ТО через ${5000 - (car.mileage - car.lastOilChangeKm)} км",
                            color = Color(0xFFFF6F00).copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }

                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFFFF6F00)
                )
            }
        }
    } else {
        // Показываем информацию о следующем ТО
        val nextServiceKm = car.lastOilChangeKm + 5000
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔧",
                        fontSize = 24.sp
                    )
                    Column {
                        Text(
                            text = "Следующее ТО",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Через ${nextServiceKm - car.mileage} км (${nextServiceKm} км)",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}