package com.autojournal.ui.screens

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autojournal.ble.SimpleOBDManager
import com.autojournal.data.preferences.SettingsPreferences
import com.autojournal.ui.theme.ThemeManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun OBDScreen(
    onBack: () -> Unit,
    onNavigateToSettings: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val prefs = remember { SettingsPreferences(context) }

    val obdManager = remember { SimpleOBDManager(context) }

    val connectionState by obdManager.connectionState.collectAsState()
    val telemetry by obdManager.telemetry.collectAsState()
    val protocolInfo by obdManager.protocolInfo.collectAsState()

    var isPolling by remember { mutableStateOf(false) }
    var isTestMode by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }
    var pairedDevices by remember { mutableStateOf<List<BluetoothDevice>>(emptyList()) }

    var selectedInterval by remember {
        mutableStateOf(prefs.getObdUpdateInterval())
    }

    val permissions = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    } else {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    val permissionState = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(Unit) {
        if (!permissionState.allPermissionsGranted) {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    fun scanPairedDevices() {
        try {
            val adapter = BluetoothAdapter.getDefaultAdapter()
            if (adapter?.isEnabled == true) {
                val devices = adapter.bondedDevices
                pairedDevices = devices.filter { device ->
                    val name = device.name?.lowercase() ?: ""
                    name.contains("obd") || name.contains("elm") || name.contains("v-link") ||
                            name.contains("car") || name.contains("obdii") || name.contains("scanner") ||
                            name.contains("diagnostic")
                }
                if (pairedDevices.isEmpty()) {
                    Toast.makeText(context, "OBD-устройства не найдены. Сопрягите адаптер в настройках Bluetooth.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Bluetooth выключен", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка сканирования: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (permissionState.allPermissionsGranted) {
            delay(500)
            scanPairedDevices()

            if (prefs.isObdAutoConnectEnabled() && pairedDevices.isNotEmpty()) {
                val device = pairedDevices.firstOrNull()
                device?.let {
                    obdManager.connect(it)
                }
            }
        }
    }

    LaunchedEffect(isPolling) {
        while (isPolling && connectionState == SimpleOBDManager.ConnectionState.READY) {
            obdManager.requestAllData()
            delay(selectedInterval)
        }
    }

    LaunchedEffect(isTestMode) {
        if (isTestMode) {
            obdManager.enableTestMode()
        } else {
            obdManager.disableTestMode()
        }
    }

    LaunchedEffect(selectedInterval) {
        obdManager.setUpdateInterval(selectedInterval)
        prefs.setObdUpdateInterval(selectedInterval)
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isTestMode) "🧪 OBD Диагностика (ТЕСТ)" else "📊 OBD Диагностика",
                        color = ThemeManager.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        obdManager.disconnect()
                        obdManager.disableTestMode()
                        obdManager.cleanup()
                        onBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = ThemeManager.accentColor
                        )
                    }
                },
                actions = {
                    // Кнопка настроек
                    IconButton(onClick = {
                        onNavigateToSettings?.invoke()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Настройки OBD",
                            tint = ThemeManager.textSecondary
                        )
                    }

                    // Кнопка тестового режима
                    IconButton(onClick = {
                        isTestMode = !isTestMode
                        if (!isTestMode) {
                            obdManager.disableTestMode()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = "Тестовый режим",
                            tint = if (isTestMode) Color.Yellow else ThemeManager.textSecondary
                        )
                    }

                    // Индикатор подключения
                    when (connectionState) {
                        SimpleOBDManager.ConnectionState.DISCONNECTED -> {
                            Icon(Icons.Default.BluetoothDisabled, null, tint = Color.Red)
                        }
                        SimpleOBDManager.ConnectionState.CONNECTING -> {
                            Icon(Icons.Default.BluetoothSearching, null, tint = Color.Yellow)
                        }
                        SimpleOBDManager.ConnectionState.CONNECTED -> {
                            Icon(Icons.Default.BluetoothConnected, null, tint = Color.Green)
                        }
                        SimpleOBDManager.ConnectionState.READY -> {
                            Icon(Icons.Default.BluetoothConnected, null, tint = Color.Green)
                        }
                        SimpleOBDManager.ConnectionState.ERROR -> {
                            Icon(Icons.Default.BluetoothDisabled, null, tint = Color.Red)
                        }
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
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ===== ИНФОРМАЦИЯ О ПРОТОКОЛЕ =====
            if (connectionState == SimpleOBDManager.ConnectionState.READY) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A237E).copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "🔧 Протокол: $protocolInfo",
                                    color = ThemeManager.accentColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Интервал: ${selectedInterval/1000}с",
                                    color = ThemeManager.textSecondary,
                                    fontSize = 12.sp
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Button(
                                    onClick = { obdManager.setKWPProtocol() },
                                    modifier = Modifier.height(28.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text("KWP", fontSize = 10.sp)
                                }
                                Button(
                                    onClick = { obdManager.setISO9141Protocol() },
                                    modifier = Modifier.height(28.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2196F3).copy(alpha = 0.2f)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text("ISO", fontSize = 10.sp)
                                }
                                Button(
                                    onClick = { obdManager.setCANProtocol() },
                                    modifier = Modifier.height(28.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFF9800).copy(alpha = 0.2f)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text("CAN", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }

            // ===== ОШИБКА ПОДКЛЮЧЕНИЯ =====
            if (connectionState == SimpleOBDManager.ConnectionState.ERROR) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Red.copy(alpha = 0.2f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "⚠️ Ошибка подключения",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Нажмите 'Восстановить' для повторного подключения",
                                color = ThemeManager.textSecondary,
                                fontSize = 12.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        obdManager.recoverConnection()
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("🔄 Восстановить")
                                }
                                Button(
                                    onClick = {
                                        obdManager.hardReset()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFF1744).copy(alpha = 0.2f)
                                    )
                                ) {
                                    Text("🔄 Хард сброс")
                                }
                            }
                        }
                    }
                }
            }

            // ===== ПРЕДУПРЕЖДЕНИЯ =====
            if (!isTestMode) {
                val adapter = BluetoothAdapter.getDefaultAdapter()
                if (adapter == null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Red.copy(alpha = 0.2f)
                            )
                        ) {
                            Text(
                                text = "⚠️ Устройство не поддерживает Bluetooth",
                                modifier = Modifier.padding(16.dp),
                                color = Color.Red
                            )
                        }
                    }
                } else if (!adapter.isEnabled) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Yellow.copy(alpha = 0.2f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(text = "⚠️ Bluetooth выключен", color = Color.Yellow)
                                Button(
                                    onClick = { adapter.enable() },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Включить Bluetooth")
                                }
                            }
                        }
                    }
                }

                if (!permissionState.allPermissionsGranted) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Yellow.copy(alpha = 0.2f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(text = "⚠️ Нет разрешений для Bluetooth", color = Color.Yellow)
                                Button(
                                    onClick = { permissionState.launchMultiplePermissionRequest() },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Запросить разрешения")
                                }
                            }
                        }
                    }
                }
            }

            // ===== ПОИСК УСТРОЙСТВ =====
            if ((connectionState == SimpleOBDManager.ConnectionState.DISCONNECTED ||
                        connectionState == SimpleOBDManager.ConnectionState.ERROR) && !isTestMode) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1E1E1E)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "🔍 Сопряжённые OBD-устройства",
                                    color = ThemeManager.textPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Button(
                                    onClick = {
                                        isScanning = true
                                        scanPairedDevices()
                                        isScanning = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ThemeManager.accentColor
                                    )
                                ) {
                                    Text(if (isScanning) "Поиск..." else "Обновить")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (pairedDevices.isEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "🔌 OBD-адаптеры не найдены",
                                        color = ThemeManager.textSecondary
                                    )
                                    Text(
                                        text = "Сопрягите адаптер в настройках Bluetooth",
                                        color = ThemeManager.textHint,
                                        fontSize = 12.sp
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.height(150.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(pairedDevices) { device ->
                                        DeviceItem(
                                            device = device,
                                            onConnect = {
                                                isTestMode = false
                                                obdManager.disableTestMode()
                                                obdManager.connect(device)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ===== ТЕЛЕМЕТРИЯ =====
            if (connectionState == SimpleOBDManager.ConnectionState.READY ||
                connectionState == SimpleOBDManager.ConnectionState.CONNECTED ||
                isTestMode) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1E1E1E)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isTestMode) "🧪 ТЕСТОВЫЕ ДАННЫЕ" else "📊 Данные телеметрии",
                                    color = ThemeManager.textPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Row {
                                    Button(
                                        onClick = { isPolling = !isPolling },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isPolling) Color.Red else ThemeManager.accentColor
                                        ),
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text(if (isPolling) "⏹️" else "▶️")
                                    }
                                    if (!isTestMode) {
                                        Button(
                                            onClick = {
                                                obdManager.requestAllData()
                                                Toast.makeText(context, "Запрос данных...", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = ThemeManager.accentColor
                                            )
                                        ) {
                                            Text("🔄")
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // ===== ПЕРВАЯ СТРОКА =====
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                TelemetryItem(
                                    icon = "🔄",
                                    value = "${telemetry.rpm.toInt()}",
                                    label = "RPM"
                                )
                                TelemetryItem(
                                    icon = "📊",
                                    value = "${telemetry.speed.toInt()}",
                                    label = "км/ч"
                                )
                                TelemetryItem(
                                    icon = "🌡️",
                                    value = "${telemetry.coolantTemp.toInt()}°",
                                    label = "Охлажд."
                                )
                                TelemetryItem(
                                    icon = "⚡",
                                    value = String.format("%.1f", telemetry.voltage),
                                    label = "Напряж."
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // ===== ВТОРАЯ СТРОКА =====
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                TelemetryItem(
                                    icon = "📈",
                                    value = "${telemetry.engineLoad.toInt()}%",
                                    label = "Нагрузка"
                                )
                                TelemetryItem(
                                    icon = "🌬️",
                                    value = "${telemetry.intakeTemp.toInt()}°",
                                    label = "Воздух"
                                )
                                TelemetryItem(
                                    icon = "🎯",
                                    value = "${telemetry.throttlePos.toInt()}%",
                                    label = "Дроссель"
                                )
                                TelemetryItem(
                                    icon = "⛽",
                                    value = if (telemetry.fuelLevel > 0) "${telemetry.fuelLevel.toInt()}%" else "—",
                                    label = "Топливо"
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // ===== ТРЕТЬЯ СТРОКА =====
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                TelemetryItem(
                                    icon = "📏",
                                    value = if (telemetry.mileage > 0) "${telemetry.mileage}" else "—",
                                    label = "Пробег км"
                                )
                                TelemetryItem(
                                    icon = "💨",
                                    value = String.format("%.1f", telemetry.mafFlow),
                                    label = "MAF г/с"
                                )
                                TelemetryItem(
                                    icon = "🔧",
                                    value = "${telemetry.fuelPressure.toInt()}",
                                    label = "Топливо кПа"
                                )
                                TelemetryItem(
                                    icon = "⏱️",
                                    value = "${telemetry.timingAdvance.toInt()}°",
                                    label = "Опереж."
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // ===== КИСЛОРОДНЫЕ ДАТЧИКИ =====
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                TelemetryItem(
                                    icon = "🔵",
                                    value = String.format("%.2f", telemetry.oxygenSensor1),
                                    label = "O2 1 V"
                                )
                                TelemetryItem(
                                    icon = "🔴",
                                    value = String.format("%.2f", telemetry.oxygenSensor2),
                                    label = "O2 2 V"
                                )
                                TelemetryItem(
                                    icon = "⏳",
                                    value = "${telemetry.engineRuntime / 60}мин",
                                    label = "Время работы"
                                )
                                TelemetryItem(
                                    icon = "⚠️",
                                    value = "${telemetry.distanceWithMIL}км",
                                    label = "С момента MIL"
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // ===== КОДЫ ОШИБОК =====
                            if (telemetry.dtcCodes.isNotEmpty()) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.Red.copy(alpha = 0.15f)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = "⚠️ Коды ошибок: ${telemetry.dtcCodes.size}",
                                            color = Color.Red,
                                            fontWeight = FontWeight.Bold
                                        )
                                        telemetry.dtcCodes.forEach { dtc ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = dtc.code,
                                                    color = Color.Red,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    text = dtc.description,
                                                    color = ThemeManager.textSecondary,
                                                    fontSize = 12.sp
                                                )
                                                Text(
                                                    text = dtc.severity,
                                                    color = when (dtc.severity) {
                                                        "Высокая" -> Color.Red
                                                        "Средняя" -> Color.Yellow
                                                        else -> Color.Green
                                                    },
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            } else if (connectionState == SimpleOBDManager.ConnectionState.READY) {
                                Text(
                                    text = "✅ Ошибок не обнаружено",
                                    color = Color.Green,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // ===== КНОПКИ ДЛЯ ОТЛАДКИ =====
                            if (!isTestMode && connectionState == SimpleOBDManager.ConnectionState.READY) {
                                // Ряд 1: Базовые команды
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { obdManager.setKWPProtocol() },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Text("KWP", fontSize = 12.sp)
                                    }
                                    Button(
                                        onClick = { obdManager.setISO9141Protocol() },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF2196F3).copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Text("ISO", fontSize = 12.sp)
                                    }
                                    Button(
                                        onClick = { obdManager.setCANProtocol() },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFF9800).copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Text("CAN", fontSize = 12.sp)
                                    }
                                }

                                // Ряд 2: Тестовые команды
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { obdManager.testKWP() },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFF6B00).copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Text("🧪 KWP Test", fontSize = 12.sp)
                                    }
                                    Button(
                                        onClick = { obdManager.requestRPM() },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ThemeManager.accentColor.copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Text("RPM", fontSize = 12.sp)
                                    }
                                    Button(
                                        onClick = { obdManager.requestVoltage() },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ThemeManager.accentColor.copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Text("Voltage", fontSize = 12.sp)
                                    }
                                }

                                // Ряд 3: Сброс и восстановление
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            obdManager.hardReset()
                                            Toast.makeText(context, "🔄 Жёсткий сброс адаптера", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFF1744).copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Text("🔄 Хард сброс", fontSize = 12.sp)
                                    }
                                    Button(
                                        onClick = {
                                            obdManager.recoverConnection()
                                            Toast.makeText(context, "🔄 Восстановление соединения", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFE65100).copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Text("🔄 Восстановить", fontSize = 12.sp)
                                    }
                                    Button(
                                        onClick = {
                                            obdManager.disconnect()
                                            isPolling = false
                                            Toast.makeText(context, "🔌 Принудительное отключение", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Red.copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Text("🔌 Отключить", fontSize = 12.sp)
                                    }
                                }

                                // Ряд 4: Дополнительные
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            obdManager.clearDTC()
                                            Toast.makeText(context, "🧹 Коды ошибок сброшены", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Red.copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Text("🧹 Сброс DTC", fontSize = 12.sp)
                                    }
                                    Button(
                                        onClick = {
                                            obdManager.requestAllData()
                                            Toast.makeText(context, "📊 Запрос всех данных", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ThemeManager.accentColor.copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Text("📊 All Data", fontSize = 12.sp)
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    obdManager.disconnect()
                                    isPolling = false
                                    if (isTestMode) {
                                        isTestMode = false
                                        obdManager.disableTestMode()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red.copy(alpha = 0.2f),
                                    contentColor = Color.Red
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            ) {
                                Text("Отключиться")
                            }
                        }
                    }
                }
            }

            // ===== ИНФОРМАЦИЯ ДЛЯ ОТЛАДКИ =====
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E).copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "🔧 Отладка",
                            color = ThemeManager.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Статус: ${connectionState.name}${if (isTestMode) " (ТЕСТ)" else ""}",
                            color = ThemeManager.textSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Протокол: $protocolInfo",
                            color = ThemeManager.textSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Интервал: ${selectedInterval/1000}с",
                            color = ThemeManager.textSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "RPM: ${telemetry.rpm.toInt()}",
                            color = ThemeManager.textSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Speed: ${telemetry.speed.toInt()}",
                            color = ThemeManager.textSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Temp: ${telemetry.coolantTemp.toInt()}°C",
                            color = ThemeManager.textSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Voltage: ${String.format("%.1f", telemetry.voltage)}V",
                            color = ThemeManager.textSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Mileage: ${if (telemetry.mileage > 0) telemetry.mileage else "—"} км",
                            color = ThemeManager.textSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Устройств: ${pairedDevices.size}",
                            color = ThemeManager.textSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceItem(
    device: BluetoothDevice,
    onConnect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = device.name ?: "Неизвестное устройство",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = device.address,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Button(
                onClick = onConnect,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ThemeManager.accentColor
                ),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Подключить", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun TelemetryItem(
    icon: String,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp)
    ) {
        Text(icon, fontSize = 20.sp)
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ThemeManager.textPrimary,
            maxLines = 1
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = ThemeManager.textSecondary,
            maxLines = 1
        )
    }
}