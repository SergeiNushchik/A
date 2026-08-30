package com.autojournal.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.autojournal.data.FuelPrices
import com.autojournal.data.model.Expense
import com.autojournal.ui.theme.ThemeManager
import com.autojournal.ui.viewmodels.DashboardViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRefuelingScreen(
    carId: String,
    viewModel: DashboardViewModel,
    onBack: () -> Unit,
    onSave: (Expense) -> Unit
) {
    // Получаем автомобиль
    val car = viewModel.cars.value.find { it.id == carId }
    val carFuelType = car?.fuelType ?: "Бензин"

    // Получаем доступные виды топлива для этого авто
    val availableFuels = FuelPrices.getFuelTypesForCar(carFuelType)
    val defaultFuel = availableFuels.firstOrNull() ?: "АИ-95"

    var selectedFuel by remember { mutableStateOf(defaultFuel) }
    var amount by remember { mutableStateOf("") }
    var pricePerLiter by remember { mutableStateOf(FuelPrices.prices[selectedFuel]?.toString() ?: "") }

    LaunchedEffect(selectedFuel) {
        pricePerLiter = FuelPrices.prices[selectedFuel]?.toString() ?: ""
    }

    val liters = run {
        val amountValue = amount.toDoubleOrNull() ?: 0.0
        val priceValue = pricePerLiter.toDoubleOrNull() ?: 0.0
        if (priceValue > 0) amountValue / priceValue else 0.0
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "⛽ Добавить заправку",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
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
            // ===== ВИД ТОПЛИВА (только доступные для авто) =====
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⛽ Вид топлива (${car?.brand} ${car?.model})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ThemeManager.textSecondary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Разбиваем на строки по 3 элемента
                    val rows = availableFuels.chunked(3)
                    rows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { fuel ->
                                FuelChip(
                                    fuel = fuel,
                                    price = FuelPrices.prices[fuel] ?: 0.0,
                                    isSelected = selectedFuel == fuel,
                                    onClick = { selectedFuel = fuel },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Заполняем пустые места, если строка неполная
                            repeat(3 - row.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // ===== ЦЕНА ЗА ЛИТР =====
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = pricePerLiter,
                        onValueChange = { pricePerLiter = it },
                        label = { Text("Цена за литр (BYN)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ThemeManager.accentColor,
                            unfocusedBorderColor = ThemeManager.accentColor.copy(alpha = 0.3f),
                            focusedLabelColor = ThemeManager.accentColor,
                            unfocusedLabelColor = ThemeManager.textSecondary
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = ThemeManager.textPrimary
                        )
                    )

                    OutlinedButton(
                        onClick = {
                            pricePerLiter = FuelPrices.prices[selectedFuel]?.toString() ?: ""
                        },
                        modifier = Modifier
                            .height(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = 1.dp,
                                color = ThemeManager.accentColor.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = ThemeManager.accentColor
                        )
                    ) {
                        Text("📊 Актуальная")
                    }
                }
            }

            // ===== СУММА =====
            item {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Сумма (BYN) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ThemeManager.accentColor,
                        unfocusedBorderColor = ThemeManager.accentColor.copy(alpha = 0.3f),
                        focusedLabelColor = ThemeManager.accentColor,
                        unfocusedLabelColor = ThemeManager.textSecondary
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = ThemeManager.textPrimary
                    )
                )
            }

            // ===== КОЛИЧЕСТВО ЛИТРОВ =====
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = ThemeManager.accentColor.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⛽ Литров",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ThemeManager.textSecondary
                        )
                        Text(
                            text = if (liters > 0) "${String.format("%.2f", liters)} л" else "0.00 л",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ThemeManager.accentColor
                        )
                    }
                }
            }

            // ===== КНОПКА СОХРАНЕНИЯ =====
            item {
                Button(
                    onClick = {
                        if (selectedFuel.isNotBlank() && amount.isNotBlank() && pricePerLiter.isNotBlank()) {
                            val expense = Expense(
                                id = UUID.randomUUID().toString(),
                                carId = carId,
                                category = "⛽ Заправка ($selectedFuel)",
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                mileage = 0,
                                description = "Заправка $selectedFuel",
                                createdAt = System.currentTimeMillis()
                            )
                            onSave(expense)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = ThemeManager.accentColor.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    enabled = selectedFuel.isNotBlank() &&
                            amount.isNotBlank() &&
                            amount.toDoubleOrNull() != null &&
                            pricePerLiter.isNotBlank() &&
                            pricePerLiter.toDoubleOrNull() != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemeManager.accentColor.copy(alpha = 0.15f),
                        contentColor = ThemeManager.accentColor,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = ThemeManager.accentColor.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = "💾 Сохранить заправку",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FuelChip(
    fuel: String,
    price: Double,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                ThemeManager.accentColor.copy(alpha = 0.2f)
            } else {
                Color(0xFF2A2A2A)
            }
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, ThemeManager.accentColor)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3A3A3A))
        },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = fuel,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) ThemeManager.accentColor else ThemeManager.textSecondary
            )
            Text(
                text = "${String.format("%.2f", price)} BYN",
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) ThemeManager.accentColor else ThemeManager.textHint
            )
        }
    }
}