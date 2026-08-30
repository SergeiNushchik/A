package com.autojournal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.autojournal.data.model.Car
import com.autojournal.data.model.Expense
import com.autojournal.ui.components.MetalBackground
import com.autojournal.ui.components.MetalCard
import com.autojournal.ui.theme.MetalChromeTheme
import com.autojournal.ui.viewmodels.DashboardViewModel
import com.autojournal.utils.formatMoney

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefuelingScreen(
    viewModel: DashboardViewModel,
    onAddRefueling: (String) -> Unit,
    onBack: () -> Unit
) {
    val cars by viewModel.cars.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val selectedCarId by viewModel.selectedCarId.collectAsState()

    val fuelExpenses = expenses.filter {
        it.category.contains("Топливо") || it.category.contains("Заправка")
    }

    var showCarDialog by remember { mutableStateOf(false) }

    val totalFuelCost = fuelExpenses.sumOf { it.amount }
    val totalFuelLiters = if (totalFuelCost > 0) totalFuelCost / 2.5 else 0.0

    MetalBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "⛽ ЗАПРАВКА",
                                color = MetalChromeTheme.MetalText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "FUEL TRACKING",
                                color = MetalChromeTheme.DarkMetalText,
                                fontSize = 10.sp,
                                letterSpacing = 4.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Назад",
                                tint = MetalChromeTheme.Chrome
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (cars.isNotEmpty()) showCarDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Добавить заправку",
                                tint = MetalChromeTheme.Chrome
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MetalChromeTheme.AnodizedBlack.copy(alpha = 0.9f)
                    )
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
                if (fuelExpenses.isNotEmpty()) {
                    item {
                        // Крупная статистика в металлическом стиле
                        MetalCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Сумма с металлическим акцентом
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = formatMoney(totalFuelCost),
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MetalChromeTheme.Chrome,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Всего потрачено",
                                        fontSize = 11.sp,
                                        color = MetalChromeTheme.DarkMetalText,
                                        letterSpacing = 1.sp
                                    )
                                }

                                // Разделитель
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(40.dp)
                                        .background(MetalChromeTheme.DarkMetalText.copy(alpha = 0.3f))
                                )

                                // Литры
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${String.format("%.1f", totalFuelLiters)} л",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MetalChromeTheme.MetalText,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Всего литров",
                                        fontSize = 11.sp,
                                        color = MetalChromeTheme.DarkMetalText,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }

                            // Нижняя хромированная линия
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .padding(top = 8.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                MetalChromeTheme.Chrome.copy(alpha = 0.3f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }
                    }
                }

                if (fuelExpenses.isEmpty()) {
                    item {
                        EmptyRefueling(onAddRefueling = {
                            if (cars.isNotEmpty()) showCarDialog = true
                        })
                    }
                } else {
                    items(fuelExpenses) { expense ->
                        RefuelingItem(expense)
                    }
                }
            }
        }
    }

    // ===== ДИАЛОГ ВЫБОРА АВТО =====
    if (showCarDialog && cars.isNotEmpty()) {
        Dialog(onDismissRequest = { showCarDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MetalChromeTheme.MetalGrey
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MetalChromeTheme.Steel.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MetalChromeTheme.MetalGrey,
                                    MetalChromeTheme.WorkshopFloor
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = "🚗 ВЫБЕРИТЕ АВТОМОБИЛЬ",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MetalChromeTheme.MetalText,
                        letterSpacing = 2.sp,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Для какого авто добавить заправку?",
                        color = MetalChromeTheme.DarkMetalText,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Divider(color = MetalChromeTheme.Steel.copy(alpha = 0.2f))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(cars) { car ->
                            CarSelectionItemForRefueling(
                                car = car,
                                onClick = {
                                    showCarDialog = false
                                    viewModel.selectCar(car.id)
                                    onAddRefueling(car.id)
                                }
                            )
                            Divider(color = MetalChromeTheme.Steel.copy(alpha = 0.1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CarSelectionItemForRefueling(
    car: Car,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MetalChromeTheme.DarkSteel.copy(alpha = 0.5f))
                .border(
                    width = 1.dp,
                    color = MetalChromeTheme.Steel.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("🚗", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "${car.brand} ${car.model}",
                color = MetalChromeTheme.MetalText,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                text = "${car.year} • ${car.plate} • ${car.mileage} км",
                color = MetalChromeTheme.DarkMetalText,
                fontSize = 11.sp
            )
        }

        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Выбрать",
            tint = MetalChromeTheme.Chrome,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun RefuelingItem(expense: Expense) {
    MetalCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = expense.description.ifEmpty { "Заправка" },
                    color = MetalChromeTheme.MetalText,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
                Text(
                    text = expense.category,
                    color = MetalChromeTheme.DarkMetalText,
                    fontSize = 10.sp
                )
            }
            Text(
                text = formatMoney(expense.amount),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MetalChromeTheme.Chrome
            )
        }
    }
}

@Composable
fun EmptyRefueling(onAddRefueling: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LocalGasStation,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MetalChromeTheme.DarkMetalText.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "НЕТ ЗАПИСЕЙ О ЗАПРАВКАХ",
            color = MetalChromeTheme.MetalText,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Нажмите ➕ чтобы добавить",
            color = MetalChromeTheme.DarkMetalText,
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        // ===== ИСПРАВЛЕННАЯ КНОПКА =====
        Button(
            onClick = onAddRefueling,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MetalChromeTheme.Chrome.copy(alpha = 0.15f),
                contentColor = MetalChromeTheme.Chrome
            ),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = MetalChromeTheme.Chrome.copy(alpha = 0.3f)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 2.dp
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "ДОБАВИТЬ ЗАПРАВКУ",
                    fontSize = 14.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}