package com.autojournal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autojournal.data.model.Expense
import com.autojournal.ui.viewmodels.DashboardViewModel
import com.autojournal.utils.formatDate
import com.autojournal.utils.formatMoney

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    carId: String,
    viewModel: DashboardViewModel,
    onBack: () -> Unit,
    onAddExpense: () -> Unit
) {
    val expenses by viewModel.expenses.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📋 История трат") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = onAddExpense) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (expenses.isEmpty()) {
                item {
                    EmptyHistory()
                }
            } else {
                items(expenses) { expense ->
                    ExpenseCard(expense)
                }
            }
        }
    }
}

@Composable
fun ExpenseCard(expense: Expense) {
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

    val categoryColor = when {
        expense.category.contains("Двигатель") -> Color(0xFFF44336)
        expense.category.contains("Ходовая") -> Color(0xFFFF9800)
        expense.category.contains("Электрика") -> Color(0xFF2196F3)
        expense.category.contains("Трансмиссия") -> Color(0xFF9C27B0)
        expense.category.contains("Кузов") -> Color(0xFF4CAF50)
        expense.category.contains("ТО") -> Color(0xFF607D8B)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = categoryColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = categoryIcon,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    if (fullPath.isNotEmpty()) {
                        Text(
                            text = fullPath,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row {
                        Text(
                            text = formatDate(expense.createdAt),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (expense.mileage > 0) {
                            Text(
                                text = " • ${expense.mileage} км",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Text(
                    text = formatMoney(expense.amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = categoryColor
                )
            }

            if (expense.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = expense.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EmptyHistory() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📭",
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "История трат пуста",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Добавьте первую запись о расходах",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}