package com.autojournal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.autojournal.data.model.RepairTask
import com.autojournal.data.RepairData
import com.autojournal.ui.theme.ThemeManager
import com.autojournal.ui.viewmodels.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRepairScreen(
    carId: String,
    viewModel: DashboardViewModel,
    onBack: () -> Unit,
    onSave: (RepairTask) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("") }
    var selectedSubCategory by remember { mutableStateOf("") }
    var selectedWork by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("medium") }
    var mileage by remember { mutableStateOf("") }

    // Получаем данные из RepairData
    val categories = RepairData.categoryList
    val subCategories = if (selectedCategory.isNotEmpty()) {
        RepairData.categories[selectedCategory]?.keys?.toList() ?: emptyList()
    } else emptyList()
    val works = if (selectedCategory.isNotEmpty() && selectedSubCategory.isNotEmpty()) {
        RepairData.categories[selectedCategory]?.get(selectedSubCategory) ?: emptyList()
    } else emptyList()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🛠️ Добавить ремонт",
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
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF1E1E1E)
            ) {
                Button(
                    onClick = {
                        if (selectedWork.isNotEmpty()) {
                            val task = RepairTask(
                                carId = carId,
                                title = selectedWork,
                                priority = selectedPriority,
                                dueDate = 0L,  // Используем 0L вместо null
                                dueMileage = mileage.toIntOrNull() ?: 0,
                                completed = false,
                                createdAt = System.currentTimeMillis()
                            )
                            onSave(task)
                        }
                    },
                    enabled = selectedWork.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemeManager.accentColor
                    )
                ) {
                    Text("✅ Сохранить")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Категории
            Text(
                text = "Выберите категорию",
                style = MaterialTheme.typography.titleMedium,
                color = ThemeManager.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Используем LazyRow для горизонтального скролла
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = {
                            selectedCategory = category
                            selectedSubCategory = ""
                            selectedWork = ""
                        },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ThemeManager.accentColor.copy(alpha = 0.2f),
                            selectedLabelColor = ThemeManager.accentColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Подкатегории
            if (subCategories.isNotEmpty()) {
                Text(
                    text = "Выберите подкатегорию",
                    style = MaterialTheme.typography.titleMedium,
                    color = ThemeManager.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(subCategories) { subCategory ->
                        FilterChip(
                            selected = selectedSubCategory == subCategory,
                            onClick = {
                                selectedSubCategory = subCategory
                                selectedWork = ""
                            },
                            label = { Text(subCategory) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ThemeManager.accentColor.copy(alpha = 0.2f),
                                selectedLabelColor = ThemeManager.accentColor
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Работы
            if (works.isNotEmpty()) {
                Text(
                    text = "Выберите работу",
                    style = MaterialTheme.typography.titleMedium,
                    color = ThemeManager.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(works) { work ->
                        FilterChip(
                            selected = selectedWork == work,
                            onClick = { selectedWork = work },
                            label = { Text(work) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ThemeManager.accentColor.copy(alpha = 0.2f),
                                selectedLabelColor = ThemeManager.accentColor
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Приоритет
            Text(
                text = "Приоритет",
                style = MaterialTheme.typography.titleMedium,
                color = ThemeManager.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "high" to "🔴 Высокий",
                    "medium" to "🟡 Средний",
                    "low" to "🟢 Низкий"
                ).forEach { (priority, label) ->
                    FilterChip(
                        selected = selectedPriority == priority,
                        onClick = { selectedPriority = priority },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ThemeManager.accentColor.copy(alpha = 0.2f),
                            selectedLabelColor = ThemeManager.accentColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Пробег
            OutlinedTextField(
                value = mileage,
                onValueChange = { mileage = it },
                label = { Text("📊 Пробег (км)") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ThemeManager.accentColor,
                    unfocusedBorderColor = ThemeManager.textHint
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}