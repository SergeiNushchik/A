package com.autojournal.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autojournal.data.model.RepairTask

@Composable
fun RepairTaskCard(
    task: RepairTask,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    onShowOnMap: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onShowOnMap() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Приоритет (используем строки, а не enum)
                    val priorityIcon = when (task.priority) {
                        "high" -> "🔴"
                        "medium" -> "🟡"
                        "low" -> "🟢"
                        else -> "⚪"
                    }
                    Text(
                        text = priorityIcon,
                        fontSize = 16.sp
                    )

                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Исправлено: теперь проверяем, что dueDate != null и это Long
                    if (task.dueDate != null && task.dueDate > 0) {
                        Text(
                            text = "📅 ${formatDateFromLong(task.dueDate)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    if (task.dueMileage != null && task.dueMileage > 0) {
                        Text(
                            text = "📊 ${task.dueMileage} км",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Кнопки действий
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Кнопка "Найти на карте"
                IconButton(
                    onClick = onShowOnMap,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Найти сервисы на карте",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Кнопка "Выполнено"
                IconButton(
                    onClick = onComplete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Выполнено",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Кнопка "Удалить"
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = Color.Red.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// Функция для форматирования Long в дату
private fun formatDateFromLong(timestamp: Long?): String {
    if (timestamp == null || timestamp == 0L) return ""
    return try {
        val date = java.util.Date(timestamp)
        java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault()).format(date)
    } catch (e: Exception) {
        ""
    }
}