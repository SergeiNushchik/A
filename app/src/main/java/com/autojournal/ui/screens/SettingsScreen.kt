package com.autojournal.ui.screens

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.autojournal.data.preferences.SettingsPreferences
import com.autojournal.ui.components.MetalBackground
import com.autojournal.ui.components.MetalCard
import com.autojournal.ui.theme.MetalChromeTheme
import com.autojournal.ui.viewmodels.DashboardViewModel
import com.autojournal.ui.widget.RefuelWidget
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.jvm.java

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: DashboardViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { SettingsPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    var isObdMode by remember { mutableStateOf(prefs.isObdModeEnabled()) }
    var isNetworkMode by remember { mutableStateOf(prefs.isNetworkModeEnabled()) }
    val deviceId by remember { mutableStateOf(prefs.getDeviceId()) }

    var isSyncing by remember { mutableStateOf(false) }
    var isClearing by remember { mutableStateOf(false) }
    var isAddingWidget by remember { mutableStateOf(false) }

    // Проверяем, есть ли уже виджет
    var hasWidget by remember { mutableStateOf(false) }
    var showWidgetInfo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(RefuelWidget::class.java)
            hasWidget = glanceIds.isNotEmpty()
        } catch (e: Exception) {
            hasWidget = false
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
                                text = "⚙️ НАСТРОЙКИ",
                                color = MetalChromeTheme.MetalText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "SYSTEM CONFIGURATION",
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
                // ===== РЕЖИМ OBD =====
                item {
                    MetalCard {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🔧 РЕЖИМ OBD-II",
                                color = MetalChromeTheme.MetalText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Работать с OBD адаптером",
                                    color = MetalChromeTheme.DarkMetalText,
                                    fontSize = 13.sp
                                )
                                MetalSwitch(
                                    checked = isObdMode,
                                    onCheckedChange = {
                                        isObdMode = it
                                        prefs.setObdModeEnabled(it)
                                    }
                                )
                            }

                            if (!isObdMode) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "⚠️ В ручном режиме данные вводятся вручную",
                                    fontSize = 11.sp,
                                    color = MetalChromeTheme.StatusYellow.copy(alpha = 0.8f),
                                    letterSpacing = 0.5.sp
                                )
                            } else {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "✅ Включите для подключения ELM327 через Bluetooth",
                                    fontSize = 11.sp,
                                    color = MetalChromeTheme.StatusGreen.copy(alpha = 0.8f),
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }

                // ===== РЕЖИМ СЕТИ =====
                item {
                    MetalCard {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🌐 РЕЖИМ СЕТИ",
                                color = MetalChromeTheme.MetalText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Синхронизация с облаком",
                                    color = MetalChromeTheme.DarkMetalText,
                                    fontSize = 13.sp
                                )
                                MetalSwitch(
                                    checked = isNetworkMode,
                                    onCheckedChange = {
                                        isNetworkMode = it
                                        prefs.setNetworkModeEnabled(it)
                                    }
                                )
                            }

                            if (!isNetworkMode) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "📴 Офлайн-режим: все данные хранятся только на устройстве",
                                    fontSize = 11.sp,
                                    color = MetalChromeTheme.StatusYellow.copy(alpha = 0.8f),
                                    letterSpacing = 0.5.sp
                                )
                            } else {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "☁️ Включите для резервного копирования на сервер",
                                    fontSize = 11.sp,
                                    color = MetalChromeTheme.StatusGreen.copy(alpha = 0.8f),
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }

                // ===== ВИДЖЕТ =====
                item {
                    MetalCard {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "📱 ВИДЖЕТ ЗАПРАВКИ",
                                color = MetalChromeTheme.MetalText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Добавьте виджет на рабочий стол для быстрого добавления заправок",
                                color = MetalChromeTheme.DarkMetalText,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Кнопка "Добавить виджет"
                            SettingsChromeButton(
                                onClick = {
                                    isAddingWidget = true
                                    try {
                                        val appWidgetManager = AppWidgetManager.getInstance(context)
                                        val provider = ComponentName(context, RefuelWidgetProvider::class.java)

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                                                // Пытаемся добавить через системный диалог
                                                val successCallback = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                    null
                                                } else {
                                                    android.app.PendingIntent.getBroadcast(
                                                        context,
                                                        0,
                                                        Intent(),
                                                        android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                                                    )
                                                }
                                                appWidgetManager.requestPinAppWidget(provider, null, successCallback)
                                                Toast.makeText(
                                                    context,
                                                    "Выберите место для виджета на рабочем столе",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                hasWidget = true
                                            } else {
                                                // Если API не поддерживается - показываем инструкцию
                                                showWidgetInfo = true
                                                Toast.makeText(
                                                    context,
                                                    "Добавьте виджет через меню виджетов",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        } else {
                                            // Для старых версий Android
                                            showWidgetInfo = true
                                            Toast.makeText(
                                                context,
                                                "Добавьте виджет через меню виджетов",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        // Если не работает - показываем инструкцию
                                        showWidgetInfo = true
                                        Toast.makeText(
                                            context,
                                            "Ошибка: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    isAddingWidget = false
                                },
                                label = if (hasWidget) "✅ ВИДЖЕТ УСТАНОВЛЕН" else "➕ ДОБАВИТЬ ВИДЖЕТ",
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isAddingWidget,
                                isLoading = isAddingWidget,
                                isSuccess = hasWidget
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Информация о виджете
                            Text(
                                text = "Виджет показывает: пробег, расход топлива, общую стоимость",
                                fontSize = 10.sp,
                                color = MetalChromeTheme.DarkMetalText.copy(alpha = 0.7f),
                                letterSpacing = 0.5.sp
                            )

                            // Кнопка "Как добавить вручную"
                            TextButton(
                                onClick = { showWidgetInfo = !showWidgetInfo },
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    text = if (showWidgetInfo) "Скрыть инструкцию" else "❓ Как добавить вручную",
                                    fontSize = 11.sp,
                                    color = MetalChromeTheme.Chrome.copy(alpha = 0.7f)
                                )
                            }

                            if (showWidgetInfo) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MetalChromeTheme.DarkSteel.copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "📌 Как добавить виджет вручную:",
                                            color = MetalChromeTheme.MetalText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "1. Нажмите и удерживайте на пустом месте рабочего стола",
                                            color = MetalChromeTheme.DarkMetalText,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = "2. Выберите «Виджеты» в появившемся меню",
                                            color = MetalChromeTheme.DarkMetalText,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = "3. Найдите «АвтоЖурнал» в списке виджетов",
                                            color = MetalChromeTheme.DarkMetalText,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = "4. Нажмите и перетащите виджет «Заправка» на рабочий стол",
                                            color = MetalChromeTheme.DarkMetalText,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ===== ИНФОРМАЦИЯ =====
                item {
                    MetalCard {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "📱 ИНФОРМАЦИЯ",
                                color = MetalChromeTheme.MetalText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            InfoRow("Device ID", "${deviceId.take(8)}...${deviceId.takeLast(4)}")
                            InfoRow("Режим OBD", if (isObdMode) "Включён" else "Выключен")
                            InfoRow("Режим сети", if (isNetworkMode) "Онлайн" else "Офлайн")
                            InfoRow("Виджет", if (hasWidget) "✅ Установлен" else "❌ Не установлен")

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Версия 1.0.0 | АвтоЖурнал 2026",
                                fontSize = 10.sp,
                                color = MetalChromeTheme.DarkMetalText,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // ===== КНОПКИ =====
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingsChromeButton(
                            onClick = {
                                isSyncing = true
                                coroutineScope.launch {
                                    delay(2000)
                                    isSyncing = false
                                }
                            },
                            label = "🔄 СИНХРОНИЗИРОВАТЬ",
                            modifier = Modifier.weight(1f),
                            enabled = isNetworkMode,
                            isLoading = isSyncing
                        )

                        SettingsChromeButton(
                            onClick = {
                                isClearing = true
                                coroutineScope.launch {
                                    delay(2000)
                                    isClearing = false
                                }
                            },
                            label = "🗑️ ОЧИСТИТЬ",
                            modifier = Modifier.weight(1f),
                            enabled = true,
                            isLoading = isClearing,
                            isDanger = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MetalChromeTheme.DarkMetalText,
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = MetalChromeTheme.MetalText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SettingsChromeButton(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    isDanger: Boolean = false,
    isSuccess: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(6.dp))
            .border(
                width = 1.dp,
                color = when {
                    isSuccess -> MetalChromeTheme.StatusGreen.copy(alpha = 0.5f)
                    isDanger && enabled -> MetalChromeTheme.StatusRed.copy(alpha = 0.5f)
                    enabled -> MetalChromeTheme.Chrome.copy(alpha = 0.5f)
                    else -> MetalChromeTheme.DarkMetalText.copy(alpha = 0.2f)
                },
                shape = RoundedCornerShape(6.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = when {
                isSuccess -> MetalChromeTheme.StatusGreen.copy(alpha = 0.15f)
                isDanger && enabled -> MetalChromeTheme.StatusRed.copy(alpha = 0.15f)
                enabled -> MetalChromeTheme.DarkSteel
                else -> MetalChromeTheme.WorkshopFloor
            },
            contentColor = when {
                isSuccess -> MetalChromeTheme.StatusGreen
                isDanger && enabled -> MetalChromeTheme.StatusRed
                enabled -> MetalChromeTheme.MetalText
                else -> MetalChromeTheme.DarkMetalText
            },
            disabledContainerColor = MetalChromeTheme.WorkshopFloor,
            disabledContentColor = MetalChromeTheme.DarkMetalText
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (enabled) 2.dp else 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (enabled && !isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = if (isDanger) 0.1f else 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            if (isLoading) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = if (isDanger) MetalChromeTheme.StatusRed else MetalChromeTheme.Chrome,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ЗАГРУЗКА...",
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = if (isDanger) MetalChromeTheme.StatusRed else MetalChromeTheme.MetalText
                    )
                }
            } else {
                Text(
                    text = label,
                    fontSize = if (isSuccess) 10.sp else 11.sp,
                    letterSpacing = 1.sp,
                    fontWeight = if (enabled) FontWeight.Medium else FontWeight.Normal,
                    color = when {
                        isSuccess -> MetalChromeTheme.StatusGreen
                        isDanger && enabled -> MetalChromeTheme.StatusRed
                        enabled -> MetalChromeTheme.MetalText
                        else -> MetalChromeTheme.DarkMetalText
                    }
                )
            }
        }
    }
}

@Composable
fun MetalSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(48.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                color = if (checked)
                    MetalChromeTheme.Chrome.copy(alpha = 0.3f)
                else
                    MetalChromeTheme.DarkSteel.copy(alpha = 0.5f)
            )
            .border(
                width = 1.dp,
                color = if (checked)
                    MetalChromeTheme.Chrome.copy(alpha = 0.5f)
                else
                    MetalChromeTheme.DarkMetalText.copy(alpha = 0.3f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onCheckedChange(!checked) }
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                .padding(3.dp)
                .clip(CircleShape)
                .background(
                    color = if (checked)
                        MetalChromeTheme.Chrome
                    else
                        MetalChromeTheme.DarkMetalText
                )
                .border(
                    width = 1.dp,
                    color = if (checked)
                        Color.White.copy(alpha = 0.3f)
                    else
                        MetalChromeTheme.FactoryDark,
                    shape = CircleShape
                )
        )
    }
}