package com.autojournal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autojournal.data.preferences.SettingsPreferences
import com.autojournal.ui.components.BoltIndicator
import com.autojournal.ui.components.MetalBackground
import com.autojournal.ui.theme.MetalChromeTheme
import com.autojournal.ui.viewmodels.DashboardViewModel

data class IndustrialMenuItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val description: String = "",
    val status: String = "●"
)

// ✅ Базовый список меню (без OBD)
val baseMenuItems = listOf(
    IndustrialMenuItem("garage", "Мой гараж", Icons.Default.DirectionsCar, "Список автомобилей"),
    IndustrialMenuItem("service", "Сервис", Icons.Default.Build, "История ремонтов"),
    IndustrialMenuItem("repairs", "Планер", Icons.Default.List, "Запланированные работы"),
    IndustrialMenuItem("refueling", "Заправка", Icons.Default.LocalGasStation, "Учёт топлива"),
    IndustrialMenuItem("statistics", "Статистика", Icons.Default.BarChart, "Расходы и аналитика"),
    IndustrialMenuItem("settings", "Настройки", Icons.Default.Settings, "Конфигурация")
)

// ✅ OBD пункт меню
val obdMenuItem = IndustrialMenuItem("obd", "OBD Диагностика", Icons.Default.Bluetooth, "Подключение к авто")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onNavigate: (String) -> Unit,
    viewModel: DashboardViewModel
) {
    val context = LocalContext.current
    val prefs = remember { SettingsPreferences(context) }

    val cars by viewModel.cars.collectAsState()
    val totalCars = cars.size

    // ✅ Проверяем включен ли OBD
    val isObdEnabled = remember { prefs.isObdModeEnabled() }

    // ✅ Формируем список меню в зависимости от настроек OBD
    val menuItems = remember(isObdEnabled) {
        if (isObdEnabled) {
            // Если OBD включен - добавляем в конец списка
            baseMenuItems + obdMenuItem
        } else {
            // Если OBD выключен - показываем только базовые пункты
            baseMenuItems
        }
    }

    MetalBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "⚙️ CarDiag Pro",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MetalChromeTheme.MetalText,
                                letterSpacing = 3.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "INDUSTRIAL EDITION",
                                    color = MetalChromeTheme.DarkMetalText,
                                    fontSize = 10.sp,
                                    letterSpacing = 4.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(RoundedCornerShape(1.dp))
                                        .background(MetalChromeTheme.StatusGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$totalCars CARS",
                                    color = MetalChromeTheme.DarkMetalText,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MetalChromeTheme.AnodizedBlack.copy(alpha = 0.9f)
                    )
                )
            }
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(menuItems) { item ->
                    IndustrialMenuItemCard(
                        item = item,
                        onClick = { onNavigate(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun IndustrialMenuItemCard(
    item: IndustrialMenuItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MetalChromeTheme.MetalGrey),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MetalChromeTheme.Steel.copy(alpha = 0.2f)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(MetalChromeTheme.MetalGrey, MetalChromeTheme.WorkshopFloor)
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    modifier = Modifier.size(48.dp),
                    tint = MetalChromeTheme.Chrome
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.title.uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MetalChromeTheme.MetalText,
                    letterSpacing = 1.sp
                )
                if (item.description.isNotEmpty()) {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MetalChromeTheme.DarkMetalText,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.status,
                    color = MetalChromeTheme.StatusGreen,
                    fontSize = 8.sp
                )
            }

            BoltIndicator(isActive = true, modifier = Modifier.size(14.dp).align(Alignment.TopStart).padding(4.dp))
            BoltIndicator(isActive = true, modifier = Modifier.size(14.dp).align(Alignment.TopEnd).padding(4.dp))
            BoltIndicator(isActive = false, modifier = Modifier.size(14.dp).align(Alignment.BottomStart).padding(4.dp))
            BoltIndicator(isActive = false, modifier = Modifier.size(14.dp).align(Alignment.BottomEnd).padding(4.dp))
        }
    }
}