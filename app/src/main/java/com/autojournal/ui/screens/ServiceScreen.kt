package com.autojournal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autojournal.data.model.Expense
import com.autojournal.ui.components.*
import com.autojournal.ui.theme.MetalChromeTheme
import com.autojournal.ui.viewmodels.DashboardViewModel
import com.autojournal.utils.formatDate
import com.autojournal.utils.formatMoney

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceScreen(
    viewModel: DashboardViewModel,
    onAddExpense: () -> Unit,
    onBack: () -> Unit
) {
    val expenses by viewModel.expenses.collectAsState()

    val serviceExpenses = expenses.filter {
        it.category.contains("Ремонт") ||
                it.category.contains("ТО") ||
                it.category.contains("Замена") ||
                it.category.contains("Двигатель") ||
                it.category.contains("Ходовая") ||
                it.category.contains("Электрика") ||
                it.category.contains("Трансмиссия") ||
                it.category.contains("Охлаждение")
    }

    val totalServiceCost = serviceExpenses.sumOf { it.amount }
    val averageCost = if (serviceExpenses.isNotEmpty()) totalServiceCost / serviceExpenses.size else 0.0

    MetalBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "🔧 СЕРВИСНОЕ ОБСЛУЖИВАНИЕ",
                                color = MetalChromeTheme.MetalText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "MAINTENANCE HISTORY",
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
                        IconButton(onClick = onAddExpense) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Добавить",
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
                if (serviceExpenses.isNotEmpty()) {
                    item {
                        MetalCard {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${serviceExpenses.size}",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MetalChromeTheme.Chrome,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Записей",
                                        fontSize = 11.sp,
                                        color = MetalChromeTheme.DarkMetalText,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(36.dp)
                                        .background(MetalChromeTheme.DarkMetalText.copy(alpha = 0.3f))
                                )

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = formatMoney(totalServiceCost),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MetalChromeTheme.MetalText,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Всего потрачено",
                                        fontSize = 11.sp,
                                        color = MetalChromeTheme.DarkMetalText,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(36.dp)
                                        .background(MetalChromeTheme.DarkMetalText.copy(alpha = 0.3f))
                                )

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = formatMoney(averageCost),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MetalChromeTheme.Titanium,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Средняя трата",
                                        fontSize = 11.sp,
                                        color = MetalChromeTheme.DarkMetalText,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }

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

                    items(serviceExpenses) { expense ->
                        ServiceItem(expense)
                    }
                } else {
                    item {
                        EmptyService(onAddExpense)
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceItem(expense: Expense) {
    val parts = expense.category.split(" → ")
    val displayName = parts.lastOrNull() ?: expense.category
    val fullPath = if (parts.size > 1) parts.joinToString(" → ") else ""

    val categoryIcon = when {
        expense.category.contains("Двигатель") -> "🔧"
        expense.category.contains("Ходовая") -> "⚙️"
        expense.category.contains("Электрика") -> "🔌"
        expense.category.contains("Охлаждение") -> "🌡️"
        expense.category.contains("Трансмиссия") -> "🛢️"
        expense.category.contains("Кузов") -> "🚗"
        expense.category.contains("ТО") -> "⛽"
        else -> "📌"
    }

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MetalChromeTheme.DarkSteel.copy(alpha = 0.5f))
                        .border(
                            width = 1.dp,
                            color = MetalChromeTheme.Steel.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = categoryIcon,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = displayName,
                        color = MetalChromeTheme.MetalText,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                    if (fullPath.isNotEmpty()) {
                        Text(
                            text = fullPath,
                            color = MetalChromeTheme.DarkMetalText,
                            fontSize = 10.sp
                        )
                    }
                    Row {
                        Text(
                            text = formatDate(expense.createdAt),
                            color = MetalChromeTheme.DarkMetalText,
                            fontSize = 10.sp
                        )
                        if (expense.mileage > 0) {
                            Text(
                                text = " • ${expense.mileage} км",
                                color = MetalChromeTheme.DarkMetalText,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Text(
                text = formatMoney(expense.amount),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MetalChromeTheme.Chrome,
                letterSpacing = 0.5.sp
            )
        }

        if (expense.description.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = expense.description,
                color = MetalChromeTheme.DarkMetalText,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 46.dp)
            )
        }
    }
}

@Composable
fun EmptyService(onAddExpense: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Build,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MetalChromeTheme.DarkMetalText.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "НЕТ ЗАПИСЕЙ ОБ ОБСЛУЖИВАНИИ",
            color = MetalChromeTheme.MetalText,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Добавьте первую запись о расходах",
            color = MetalChromeTheme.DarkMetalText,
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        ChromeSmallButton(
            onClick = onAddExpense,
            label = "➕ ДОБАВИТЬ ЗАПИСЬ",
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
    }
}