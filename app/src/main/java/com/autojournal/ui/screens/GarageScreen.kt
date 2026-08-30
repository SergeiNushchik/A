package com.autojournal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.autojournal.data.model.Car
import com.autojournal.ui.theme.ThemeManager
import com.autojournal.ui.viewmodels.DashboardViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarageScreen(
    viewModel: DashboardViewModel,
    onCarSelect: (String) -> Unit,  // ← ЭТОТ ПАРАМЕТР БУДЕТ ВЫЗЫВАТЬСЯ ПРИ КЛИКЕ НА АВТО
    onAddCar: () -> Unit,
    onEditCar: (String) -> Unit,
    onBack: () -> Unit
) {
    val cars by viewModel.cars.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var carToDelete by remember { mutableStateOf<Car?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("🚗 Мой гараж", color = ThemeManager.textPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад", tint = ThemeManager.accentColor)
                    }
                },
                actions = {
                    IconButton(onClick = onAddCar) {
                        Icon(Icons.Default.Add, "Добавить авто", tint = ThemeManager.accentColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        if (cars.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.DirectionsCar,
                        null,
                        Modifier.size(64.dp),
                        tint = ThemeManager.textHint
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Гараж пуст",
                        style = MaterialTheme.typography.titleMedium,
                        color = ThemeManager.textPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = onAddCar,
                        colors = ButtonDefaults.buttonColors(containerColor = ThemeManager.accentColor)
                    ) {
                        Text("➕ Добавить автомобиль")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cars) { car ->
                    CarCard(
                        car = car,
                        onClick = {
                            // ✅ ПРИ КЛИКЕ НА АВТО - ОТКРЫВАЕМ ИНФОРМАЦИЮ
                            onCarSelect(car.id)
                        },
                        onEdit = { onEditCar(car.id) },
                        onDelete = {
                            carToDelete = car
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    // ===== ДИАЛОГ ПОДТВЕРЖДЕНИЯ УДАЛЕНИЯ =====
    if (showDeleteDialog && carToDelete != null) {
        Dialog(onDismissRequest = {
            showDeleteDialog = false
            carToDelete = null
        }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            null,
                            modifier = Modifier.size(48.dp),
                            tint = ThemeManager.accentColor.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Удалить автомобиль?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ThemeManager.textPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Вы уверены, что хотите удалить ${carToDelete?.brand} ${carToDelete?.model} (${carToDelete?.plate})?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ThemeManager.textSecondary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Все данные по этому автомобилю будут потеряны!",
                        style = MaterialTheme.typography.bodySmall,
                        color = ThemeManager.accentColor.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                showDeleteDialog = false
                                carToDelete = null
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = ThemeManager.textSecondary
                            )
                        ) {
                            Text("Отмена")
                        }

                        Button(
                            onClick = {
                                carToDelete?.let { car ->
                                    viewModel.deleteCar(car)
                                }
                                showDeleteDialog = false
                                carToDelete = null
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFB71C1C),
                                contentColor = Color.White
                            )
                        ) {
                            Text("🗑️ Удалить")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CarCard(
    car: Car,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },  // ← ПРИ КЛИКЕ НА КАРТОЧКУ - ВЫЗЫВАЕМ ONCLICK
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Фото автомобиля (если есть)
            if (!car.photoUrl.isNullOrEmpty()) {
                val photoFile = File(car.photoUrl)
                if (photoFile.exists()) {
                    AsyncImage(
                        model = photoFile,
                        contentDescription = "Фото ${car.brand} ${car.model}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentScale = ContentScale.Crop
                    )
                    // Затемнение
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .background(Color.Black.copy(alpha = 0.4f))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Color(0xFF2A2A2A))
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color(0xFF2A2A2A))
                )
            }

            // Информация об авто поверх фото
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${car.brand} ${car.model}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            car.plate,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ThemeManager.accentColor
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                "📅 ${car.year}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                "⛽ ${car.fuelType}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                "📊 ${car.mileage} км",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Кнопки управления
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(onClick = onEdit) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Редактировать",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Удалить",
                                tint = Color(0xFFEF5350),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}