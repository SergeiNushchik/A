package com.autojournal.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autojournal.data.RepairShop
import com.autojournal.data.RepairShops

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopMapScreen(
    taskTitle: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // ✅ АКТИВНЫЙ КОД: Офлайн-поиск по базе Бреста
    val searchKeywords = remember(taskTitle) {
        val words = taskTitle.split(" ", "(", ")", "—", "-")
        words.find { it.length > 3 } ?: "автосервис"
    }

    val shops = remember(searchKeywords) {
        RepairShops.findShopsByService(searchKeywords)
    }

    var selectedShop by remember { mutableStateOf<RepairShop?>(null) }

    // ========================================================================
    // ⛔ ВРЕМЕННО ОТКЛЮЧЕНО: ГЕОЛОКАЦИЯ И YANDEX SEARCH API
    // Причина: Ошибки компиляции Unresolved reference: createSearchManager / await
    // Для включения раскомментируйте блок ниже и добавьте импорты MapKit Search
    // ========================================================================
    /*
    var userLocation by remember { mutableStateOf<Point?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var searchError by remember { mutableStateOf<String?>(null) }
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                val tokenSource = CancellationTokenSource()
                val location: android.location.Location? = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    tokenSource.token
                ).await()
                location?.let { loc ->
                    userLocation = Point(loc.latitude, loc.longitude)
                } ?: run { searchError = "Не удалось определить местоположение" }
            } catch (e: Exception) {
                searchError = "Ошибка геолокации: ${e.message}"
            } finally { isLoading = false }
        } else {
            locationPermissionState.launchPermissionRequest()
            isLoading = false
        }
    }

    LaunchedEffect(userLocation, searchKeywords) {
        val loc = userLocation ?: return@LaunchedEffect
        isLoading = true
        searchError = null
        try {
            val searchManager = MapKitFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
            val geometry = Geometry.fromPoint(loc)
            val searchOptions = SearchOptions().apply {
                resultPageSize = 20
                searchTypes = SearchType.BIZ.value
            }
            searchManager.submit(searchKeywords, geometry, searchOptions, object : SearchSession.SearchListener {
                override fun onSearchResponse(response: SearchResponse) {
                    // Парсинг ответа...
                    isLoading = false
                }
                override fun onSearchError(error: com.yandex.runtime.Error) {
                    searchError = "Ошибка поиска: ${error.message}"
                    isLoading = false
                }
            })
        } catch (e: Exception) {
            searchError = "Критическая ошибка: ${e.message}"
            isLoading = false
        }
    }
    */
    // ========================================================================

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("🔧 Сервисы по ремонту", color = Color.White)
                        Text(
                            text = "Поиск: \"$searchKeywords\" • Найдено: ${shops.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color(0xFF4CAF50))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E1E1E))
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ✅ АКТИВНЫЙ КОД: Простой список без карты
            if (shops.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SearchOff, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("Сервисы не найдены", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Попробуйте другой запрос", color = Color.Gray, fontSize = 12.sp)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50).copy(alpha = 0.15f),
                                contentColor = Color(0xFF4CAF50)
                            )
                        ) { Text("🔙 Вернуться") }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(shops) { shop ->
                        OfflineShopCard(
                            shop = shop,
                            onClick = { selectedShop = shop },
                            onCall = {
                                try {
                                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${shop.phone}")))
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Не удалось позвонить", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onNavigate = {
                                try {
                                    val uri = Uri.parse("yandexmaps://build_route_on_map?lat_to=${shop.lat}&lon_to=${shop.lng}")
                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                } catch (_: Exception) {
                                    val webUri = Uri.parse("https://yandex.ru/maps/?pt=${shop.lng},${shop.lat}&z=15")
                                    context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Диалог информации о выбранном СТО
    if (selectedShop != null) {
        AlertDialog(
            onDismissRequest = { selectedShop = null },
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color.Gray,
            title = { Text(selectedShop!!.name, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("📍 ${selectedShop!!.address}")
                    Text("⭐ ${selectedShop!!.rating} • 💰 ${selectedShop!!.price}")
                    Text("🕒 ${selectedShop!!.workingHours}")
                    Text("🔧 ${selectedShop!!.services.joinToString(", ")}", fontSize = 12.sp)
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        try {
                            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${selectedShop!!.phone}")))
                        } catch (_: Exception) {}
                    }) { Text("📞 Звонок", color = Color(0xFF4CAF50)) }
                    TextButton(onClick = {
                        try {
                            val uri = Uri.parse("yandexmaps://build_route_on_map?lat_to=${selectedShop!!.lat}&lon_to=${selectedShop!!.lng}")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        } catch (_: Exception) {
                            val webUri = Uri.parse("https://yandex.ru/maps/?pt=${selectedShop!!.lng},${selectedShop!!.lat}&z=15")
                            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                        }
                    }) { Text("🗺️ Маршрут", color = Color(0xFF4CAF50)) }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedShop = null }) { Text("Закрыть", color = Color.Gray) }
            }
        )
    }
}

@Composable
private fun OfflineShopCard(
    shop: RepairShop,
    onClick: () -> Unit,
    onCall: () -> Unit,
    onNavigate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(shop.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White)
                Text(shop.address, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("⭐ ${shop.rating}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50))
                    Text("💰 ${shop.price}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onCall, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Phone, "Позвонить", tint = Color(0xFF4CAF50), modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onNavigate, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Route, "Маршрут", tint = Color(0xFF4CAF50), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}