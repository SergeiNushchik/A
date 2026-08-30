package com.autojournal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autojournal.data.preferences.SettingsPreferences
import com.autojournal.ui.theme.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObdSettingsScreen(
    prefs: SettingsPreferences,
    onBack: () -> Unit
) {
    var activePids by remember {
        mutableStateOf(prefs.getActiveObdPids())
    }

    var updateInterval by remember {
        mutableStateOf(prefs.getObdUpdateInterval())
    }

    var timeout by remember {
        mutableStateOf(prefs.getObdTimeout())
    }

    var connectionAttempts by remember {
        mutableStateOf(prefs.getObdConnectionAttempts())
    }

    var autoConnect by remember {
        mutableStateOf(prefs.isObdAutoConnectEnabled())
    }

    var selectedProtocol by remember {
        mutableStateOf(prefs.getObdProtocol())
    }

    val allPids = prefs.getAllAvailablePids()
    val protocols = prefs.getProtocolsList()
    var expandedProtocol by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "⚙️ Настройки OBD",
                        color = ThemeManager.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // Сохраняем настройки
                        prefs.setActiveObdPids(activePids)
                        prefs.setObdUpdateInterval(updateInterval)
                        prefs.setObdTimeout(timeout)
                        prefs.setObdConnectionAttempts(connectionAttempts)
                        prefs.setObdAutoConnectEnabled(autoConnect)
                        prefs.setObdProtocol(selectedProtocol)
                        val protocolName = protocols.find { it.command == selectedProtocol }?.name ?: "Автоопределение"
                        prefs.setObdProtocolName(protocolName)
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
                    TextButton(
                        onClick = {
                            // Сброс к дефолтным настройкам
                            activePids = setOf(
                                "010C", "010D", "0105", "ATRV", "0104", "010F", "0111"
                            )
                            updateInterval = 1000L
                            timeout = 10
                            connectionAttempts = 3
                            autoConnect = false
                            selectedProtocol = "ATSP0"

                            prefs.setActiveObdPids(activePids)
                            prefs.setObdUpdateInterval(updateInterval)
                            prefs.setObdTimeout(timeout)
                            prefs.setObdConnectionAttempts(connectionAttempts)
                            prefs.setObdAutoConnectEnabled(autoConnect)
                            prefs.setObdProtocol(selectedProtocol)
                            prefs.setObdProtocolName("Автоопределение")
                        }
                    ) {
                        Text("Сброс", color = ThemeManager.accentColor)
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
            // ===== ОБЩИЕ НАСТРОЙКИ =====
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
                        Text(
                            text = "🔧 Общие настройки",
                            color = ThemeManager.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // ===== ВЫБОР ПРОТОКОЛА =====
                        Text(
                            text = "📡 Протокол OBD-II",
                            color = ThemeManager.textSecondary,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandedProtocol,
                            onExpandedChange = { expandedProtocol = it }
                        ) {
                            OutlinedTextField(
                                value = protocols.find { it.command == selectedProtocol }?.name ?: "Автоопределение",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProtocol) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ThemeManager.accentColor,
                                    unfocusedBorderColor = ThemeManager.textHint
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedProtocol,
                                onDismissRequest = { expandedProtocol = false }
                            ) {
                                protocols.forEach { protocol ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = protocol.name,
                                                    color = if (selectedProtocol == protocol.command) ThemeManager.accentColor else ThemeManager.textPrimary
                                                )
                                                Text(
                                                    text = protocol.description,
                                                    fontSize = 10.sp,
                                                    color = ThemeManager.textSecondary
                                                )
                                                if (protocol.speed.isNotEmpty()) {
                                                    Text(
                                                        text = "Скорость: ${protocol.speed}",
                                                        fontSize = 9.sp,
                                                        color = ThemeManager.textHint
                                                    )
                                                }
                                            }
                                        },
                                        onClick = {
                                            selectedProtocol = protocol.command
                                            expandedProtocol = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // ===== ИНТЕРВАЛ ОБНОВЛЕНИЯ =====
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Интервал обновления",
                                color = ThemeManager.textSecondary,
                                fontSize = 14.sp
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(500L, 1000L, 2000L, 5000L).forEach { interval ->
                                    FilterChip(
                                        selected = updateInterval == interval,
                                        onClick = { updateInterval = interval },
                                        label = { Text("${interval/1000}с", fontSize = 11.sp) },
                                        modifier = Modifier.height(32.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = ThemeManager.accentColor.copy(alpha = 0.2f),
                                            selectedLabelColor = ThemeManager.accentColor
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // ===== ТАЙМАУТ =====
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Таймаут (сек)",
                                color = ThemeManager.textSecondary,
                                fontSize = 14.sp
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(5, 10, 15, 20).forEach { t ->
                                    FilterChip(
                                        selected = timeout == t,
                                        onClick = { timeout = t },
                                        label = { Text("$t", fontSize = 11.sp) },
                                        modifier = Modifier.height(32.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = ThemeManager.accentColor.copy(alpha = 0.2f),
                                            selectedLabelColor = ThemeManager.accentColor
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // ===== ПОПЫТКИ ПОДКЛЮЧЕНИЯ =====
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Попыток подключения",
                                color = ThemeManager.textSecondary,
                                fontSize = 14.sp
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(1, 2, 3, 5).forEach { attempts ->
                                    FilterChip(
                                        selected = connectionAttempts == attempts,
                                        onClick = { connectionAttempts = attempts },
                                        label = { Text("$attempts", fontSize = 11.sp) },
                                        modifier = Modifier.height(32.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = ThemeManager.accentColor.copy(alpha = 0.2f),
                                            selectedLabelColor = ThemeManager.accentColor
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // ===== АВТОПОДКЛЮЧЕНИЕ =====
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Автоподключение при запуске",
                                color = ThemeManager.textSecondary,
                                fontSize = 14.sp
                            )
                            Switch(
                                checked = autoConnect,
                                onCheckedChange = { autoConnect = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ThemeManager.accentColor,
                                    checkedTrackColor = ThemeManager.accentColor.copy(alpha = 0.4f)
                                )
                            )
                        }
                    }
                }
            }

            // ===== ПАРАМЕТРЫ =====
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
                        Text(
                            text = "📊 Параметры диагностики",
                            color = ThemeManager.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Text(
                            text = "Включите параметры, которые хотите отслеживать",
                            color = ThemeManager.textSecondary,
                            fontSize = 12.sp
                        )

                        // Подсказка о поддержке в зависимости от протокола
                        val protocol = protocols.find { it.command == selectedProtocol }
                        when {
                            selectedProtocol == "ATSP6" || selectedProtocol == "ATSP7" ||
                                    selectedProtocol.startsWith("ATSP9") || selectedProtocol.startsWith("ATSP10") -> {
                                Text(
                                    text = "✅ CAN протокол - доступны все параметры",
                                    color = Color.Green,
                                    fontSize = 11.sp
                                )
                            }
                            selectedProtocol == "ATSP3" || selectedProtocol == "ATSP4" || selectedProtocol == "ATSP5" -> {
                                Text(
                                    text = "⚠️ KWP/ISO 9141-2 - некоторые параметры могут не работать (Fuel, Mileage, MAF, Pressure, Timing)",
                                    color = Color.Yellow,
                                    fontSize = 11.sp
                                )
                            }
                            selectedProtocol == "ATSP0" -> {
                                Text(
                                    text = "🔍 Автоопределение - параметры будут определяться автоматически",
                                    color = Color.Cyan,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        allPids.forEach { config ->
                            // Для неподдерживаемых PID на старых протоколах показываем предупреждение
                            val isSupported = when {
                                selectedProtocol == "ATSP6" || selectedProtocol == "ATSP7" ||
                                        selectedProtocol.startsWith("ATSP9") || selectedProtocol.startsWith("ATSP10") -> true
                                selectedProtocol == "ATSP3" || selectedProtocol == "ATSP4" || selectedProtocol == "ATSP5" -> {
                                    !listOf("012F", "0146", "0110", "010A", "010E").contains(config.pid)
                                }
                                else -> true
                            }

                            ObdPidItem(
                                config = config,
                                isChecked = activePids.contains(config.pid),
                                isSupported = isSupported,
                                onToggle = {
                                    val newSet = activePids.toMutableSet()
                                    if (newSet.contains(config.pid)) {
                                        newSet.remove(config.pid)
                                    } else {
                                        newSet.add(config.pid)
                                    }
                                    activePids = newSet
                                }
                            )
                        }
                    }
                }
            }

            // ===== ИНФОРМАЦИЯ =====
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E).copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "ℹ️ Информация о протоколах",
                            color = ThemeManager.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "• ISO 9141-2 / KWP - Peugeot, Citroën, Fiat, Opel, VW/Audi до 2005",
                            color = ThemeManager.textSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "• CAN - большинство автомобилей после 2008 (все параметры доступны)",
                            color = ThemeManager.textSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "• J1850 PWM - Ford, Jaguar до 2004",
                            color = ThemeManager.textSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "• J1850 VPW - GM, Chrysler до 2004",
                            color = ThemeManager.textSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Рекомендуемые параметры отмечены значком ★",
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
fun ObdPidItem(
    config: SettingsPreferences.ObdPidInfo,
    isChecked: Boolean,
    isSupported: Boolean = true,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isChecked)
                ThemeManager.accentColor.copy(alpha = 0.1f)
            else
                Color(0xFF2A2A2A)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = config.icon,
                    fontSize = 20.sp
                )
                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = config.name,
                            color = if (isChecked) ThemeManager.textPrimary else ThemeManager.textSecondary,
                            fontSize = 14.sp
                        )
                        if (config.recommended) {
                            Text(
                                text = "★",
                                color = Color.Yellow,
                                fontSize = 12.sp
                            )
                        }
                        if (!isSupported) {
                            Text(
                                text = "⚠️",
                                color = Color.Yellow,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Text(
                        text = "PID: ${config.pid}${if (!isSupported) " (может не работать)" else ""}",
                        color = ThemeManager.textHint,
                        fontSize = 10.sp
                    )
                }
            }

            Switch(
                checked = isChecked,
                onCheckedChange = {
                    if (!isSupported && !isChecked) {
                        // Предупреждение о неподдерживаемом PID
                    }
                    onToggle()
                },
                enabled = true,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ThemeManager.accentColor,
                    checkedTrackColor = ThemeManager.accentColor.copy(alpha = 0.4f),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.DarkGray
                )
            )
        }
    }
}