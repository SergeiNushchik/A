package com.autojournal.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.autojournal.data.ExpenseCategories
import com.autojournal.data.model.Expense
import com.autojournal.ui.viewmodels.DashboardViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    carId: String,
    viewModel: DashboardViewModel,
    onBack: () -> Unit,
    onSave: (Expense) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("") }
    var selectedSubcategory by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf("") }

    var amount by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var showCategoryDialog by remember { mutableStateOf(false) }
    var showSubcategoryDialog by remember { mutableStateOf(false) }
    var showItemDialog by remember { mutableStateOf(false) }

    val subcategories = if (selectedCategory.isNotEmpty()) {
        ExpenseCategories.categories[selectedCategory]?.keys?.toList() ?: emptyList()
    } else {
        emptyList()
    }

    val items = if (selectedCategory.isNotEmpty() && selectedSubcategory.isNotEmpty()) {
        ExpenseCategories.categories[selectedCategory]?.get(selectedSubcategory) ?: emptyList()
    } else {
        emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("➕ Добавить трату") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
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
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Категория *") },
                        placeholder = { Text("Нажмите для выбора") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            if (selectedCategory.isNotEmpty()) {
                                IconButton(onClick = {
                                    selectedCategory = ""
                                    selectedSubcategory = ""
                                    selectedItem = ""
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Очистить")
                                }
                            } else {
                                Text("▼", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .matchParentSize()
                            .clickable { showCategoryDialog = true }
                    )
                }
            }

            if (selectedCategory.isNotEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedSubcategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Подкатегория *") },
                            placeholder = { Text("Нажмите для выбора") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedCategory.isNotEmpty(),
                            trailingIcon = {
                                if (selectedSubcategory.isNotEmpty()) {
                                    IconButton(onClick = {
                                        selectedSubcategory = ""
                                        selectedItem = ""
                                    }) {
                                        Icon(Icons.Default.Close, contentDescription = "Очистить")
                                    }
                                } else if (selectedCategory.isNotEmpty()) {
                                    Text("▼", style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .matchParentSize()
                                .clickable {
                                    if (selectedCategory.isNotEmpty()) {
                                        showSubcategoryDialog = true
                                    }
                                }
                        )
                    }
                }
            }

            if (selectedSubcategory.isNotEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedItem,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Позиция *") },
                            placeholder = { Text("Нажмите для выбора") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedSubcategory.isNotEmpty(),
                            trailingIcon = {
                                if (selectedItem.isNotEmpty()) {
                                    IconButton(onClick = { selectedItem = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Очистить")
                                    }
                                } else if (selectedSubcategory.isNotEmpty()) {
                                    Text("▼", style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .matchParentSize()
                                .clickable {
                                    if (selectedSubcategory.isNotEmpty()) {
                                        showItemDialog = true
                                    }
                                }
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Сумма (BYN) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = mileage,
                    onValueChange = { mileage = it },
                    label = { Text("Пробег (км)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание (дополнительно)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (selectedCategory.isNotEmpty() && selectedSubcategory.isNotEmpty() && selectedItem.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "📋 Выбрано:",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = "$selectedCategory → $selectedSubcategory → $selectedItem",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        if (selectedCategory.isNotBlank() &&
                            selectedSubcategory.isNotBlank() &&
                            selectedItem.isNotBlank() &&
                            amount.isNotBlank()) {

                            val expense = Expense(
                                id = UUID.randomUUID().toString(),
                                carId = carId,
                                category = "$selectedCategory → $selectedSubcategory → $selectedItem",
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                mileage = mileage.toIntOrNull() ?: 0,
                                description = description,
                                createdAt = System.currentTimeMillis()
                            )
                            onSave(expense)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = selectedCategory.isNotBlank() &&
                            selectedSubcategory.isNotBlank() &&
                            selectedItem.isNotBlank() &&
                            amount.isNotBlank()
                ) {
                    Text("💾 Сохранить", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }

    if (showCategoryDialog) {
        Dialog(onDismissRequest = { showCategoryDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column {
                    Text(
                        text = "Выберите категорию",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    Divider()
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(ExpenseCategories.categoryList) { category ->
                            ListItem(
                                headlineContent = { Text(category) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCategory = category
                                        selectedSubcategory = ""
                                        selectedItem = ""
                                        showCategoryDialog = false
                                    }
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }

    if (showSubcategoryDialog && subcategories.isNotEmpty()) {
        Dialog(onDismissRequest = { showSubcategoryDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column {
                    Text(
                        text = "Выберите подкатегорию ($selectedCategory)",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    Divider()
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(subcategories) { subcategory ->
                            ListItem(
                                headlineContent = { Text(subcategory) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedSubcategory = subcategory
                                        selectedItem = ""
                                        showSubcategoryDialog = false
                                    }
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }

    if (showItemDialog && items.isNotEmpty()) {
        Dialog(onDismissRequest = { showItemDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column {
                    Text(
                        text = "Выберите позицию ($selectedSubcategory)",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    Divider()
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items) { item ->
                            ListItem(
                                headlineContent = { Text(item) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedItem = item
                                        showItemDialog = false
                                    }
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}