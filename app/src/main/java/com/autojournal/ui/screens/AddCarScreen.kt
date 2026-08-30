package com.autojournal.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.autojournal.data.CarDatabaseLoader
import com.autojournal.data.model.CarTrim
import com.autojournal.ui.theme.ThemeManager
import com.autojournal.ui.viewmodels.DashboardViewModel
import com.autojournal.utils.ImageUtils
import com.yalantis.ucrop.UCrop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCarScreen(
    viewModel: DashboardViewModel,
    onBack: () -> Unit,
    onCarAdded: (String) -> Unit = {},
    editCarId: String? = null
) {
    val context = LocalContext.current
    val isEditMode = editCarId != null

    val existingCar = remember(editCarId) {
        editCarId?.let { id -> viewModel.cars.value.find { it.id == id } }
    }

    // ===== СОСТОЯНИЯ =====
    var selectedBrand by remember { mutableStateOf(existingCar?.brand ?: "") }
    var selectedModel by remember { mutableStateOf(existingCar?.model ?: "") }
    var selectedGeneration by remember { mutableStateOf("") }
    var selectedTrim by remember { mutableStateOf<CarTrim?>(null) }

    var year by remember { mutableStateOf(existingCar?.year?.toString() ?: "") }
    var plate by remember { mutableStateOf(existingCar?.plate ?: "") }
    var mileage by remember { mutableStateOf(existingCar?.mileage?.toString() ?: "") }

    // ===== СОСТОЯНИЕ ДЛЯ ФОТО =====
    var carPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var isPhotoLoading by remember { mutableStateOf(false) }
    var originalPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // ===== ПОИСКОВЫЕ ЗАПРОСЫ =====
    var brandQuery by remember { mutableStateOf(selectedBrand) }
    var modelQuery by remember { mutableStateOf(selectedModel) }
    var generationQuery by remember { mutableStateOf(selectedGeneration) }
    var trimSearchQuery by remember { mutableStateOf("") }

    // ===== ФЛАГИ ВИДИМОСТИ =====
    var showBrands by remember { mutableStateOf(false) }
    var showModels by remember { mutableStateOf(false) }
    var showGenerationSuggestions by remember { mutableStateOf(false) }
    var showTrimDialog by remember { mutableStateOf(false) }
    var showPhotoDialog by remember { mutableStateOf(false) }

    // ===== ДАННЫЕ ИЗ JSON =====
    val allBrands = remember { CarDatabaseLoader.getBrandNames(context) }

    val availableModels = remember(selectedBrand) {
        if (selectedBrand.isNotEmpty()) CarDatabaseLoader.getModelsForBrand(context, selectedBrand)
        else emptyList()
    }

    val availableGenerations = remember(selectedBrand, selectedModel) {
        if (selectedBrand.isNotEmpty() && selectedModel.isNotEmpty())
            CarDatabaseLoader.getGenerationsForModel(context, selectedBrand, selectedModel)
        else emptyList()
    }

    val availableTrims = remember(selectedBrand, selectedModel, selectedGeneration) {
        if (selectedBrand.isNotEmpty() && selectedModel.isNotEmpty() && selectedGeneration.isNotEmpty()) {
            CarDatabaseLoader.loadBrands(context)
                .find { it.name == selectedBrand }
                ?.models?.find { it.name == selectedModel }
                ?.generations?.find { it.name == selectedGeneration }
                ?.trims ?: emptyList()
        } else emptyList()
    }

    // ===== ФИЛЬТРАЦИЯ =====
    val filteredBrands = remember(brandQuery, allBrands) {
        if (brandQuery.isBlank()) allBrands.take(30)
        else allBrands.filter { it.contains(brandQuery, ignoreCase = true) }.take(30)
    }

    val filteredModels = remember(modelQuery, availableModels) {
        if (modelQuery.isBlank()) availableModels.take(30)
        else availableModels.filter { it.contains(modelQuery, ignoreCase = true) }.take(30)
    }

    val filteredGenerations = remember(generationQuery, availableGenerations) {
        if (generationQuery.isBlank()) availableGenerations
        else availableGenerations.filter {
            it.first.contains(generationQuery, ignoreCase = true) ||
                    it.second.contains(generationQuery, ignoreCase = true)
        }
    }

    val filteredTrims = remember(trimSearchQuery, availableTrims) {
        if (trimSearchQuery.isBlank()) availableTrims
        else availableTrims.filter {
            it.name.contains(trimSearchQuery, ignoreCase = true) ||
                    it.engine.code.contains(trimSearchQuery, ignoreCase = true)
        }
    }

    // ===== ЛАУНЧЕР ДЛЯ UCROP =====
    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            when (result.resultCode) {
                android.app.Activity.RESULT_OK -> {
                    val data = result.data
                    if (data != null) {
                        val resultUri = UCrop.getOutput(data)
                        resultUri?.let {
                            carPhotoUri = it
                            isPhotoLoading = false
                            Toast.makeText(context, "Фото обрезано", Toast.LENGTH_SHORT).show()
                        } ?: run {
                            Toast.makeText(context, "Не удалось получить обрезанное фото", Toast.LENGTH_SHORT).show()
                            isPhotoLoading = false
                        }
                    } else {
                        Toast.makeText(context, "Ошибка: данные не получены", Toast.LENGTH_SHORT).show()
                        isPhotoLoading = false
                    }
                }
                UCrop.RESULT_ERROR -> {
                    val data = result.data
                    val error = if (data != null) UCrop.getError(data) else null
                    Toast.makeText(context, "Ошибка: ${error?.message ?: "неизвестная ошибка"}", Toast.LENGTH_SHORT).show()
                    isPhotoLoading = false
                }
                else -> {
                    isPhotoLoading = false
                }
            }
        }
    )



    // ===== ФУНКЦИЯ ДЛЯ ЗАПУСКА UCROP =====
    fun startCropWithLauncher(sourceUri: Uri) {
        val destinationUri = ImageUtils.getCropOutputUri(context)

        val options = UCrop.Options()
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
        options.setCompressionQuality(90)
        options.setHideBottomControls(false)
        options.setFreeStyleCropEnabled(true)
        options.setShowCropGrid(true)
        options.setShowCropFrame(true)
        options.setCircleDimmedLayer(false)
        options.setToolbarTitle("Обрезка фото")
        // Используем toInt() для преобразования цвета
        options.setToolbarColor(android.graphics.Color.parseColor("#1E1E1E"))
        options.setStatusBarColor(android.graphics.Color.parseColor("#1E1E1E"))
        options.setToolbarWidgetColor(android.graphics.Color.WHITE)
        options.setDimmedLayerColor(android.graphics.Color.parseColor("#AA000000"))

        val uCrop = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(16f, 9f)
            .withMaxResultSize(1920, 1080)
            .withOptions(options)

        val intent = uCrop.getIntent(context)
        cropLauncher.launch(intent)
    }
    // ✅ 3. ЛАУНЧЕР ДЛЯ ВЫБОРА ФОТО (объявлен ПОСЛЕДНИМ)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                originalPhotoUri = it
                startCropWithLauncher(it)
            }
        }
    )

    fun resetFromBrand() {
        selectedModel = ""
        modelQuery = ""
        selectedGeneration = ""
        generationQuery = ""
        selectedTrim = null
        trimSearchQuery = ""
    }

    fun resetFromModel() {
        selectedGeneration = ""
        generationQuery = ""
        selectedTrim = null
        trimSearchQuery = ""
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "✏️ Редактировать авто" else "➕ Добавить автомобиль") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
            // ===== ФОТО АВТОМОБИЛЯ =====
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clickable { showPhotoDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (carPhotoUri != null) {
                            AsyncImage(
                                model = carPhotoUri,
                                contentDescription = "Фото автомобиля",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f))
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.PhotoCamera,
                                    contentDescription = "Изменить фото",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Изменить фото",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                                )
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.PhotoCamera,
                                    contentDescription = "Добавить фото",
                                    tint = ThemeManager.textHint,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Нажмите, чтобы добавить фото",
                                    color = ThemeManager.textHint,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                                )
                            }
                        }
                    }
                }
            }

            // ===== 1. МАРКА =====
            item {
                Column {
                    OutlinedTextField(
                        value = brandQuery,
                        onValueChange = {
                            brandQuery = it
                            showBrands = true
                            if (it.isBlank()) {
                                selectedBrand = ""
                                resetFromBrand()
                            }
                        },
                        label = { Text("Марка *") },
                        placeholder = { Text("Выберите или начните вводить") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { showBrands = it.isFocused },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, "Поиск") },
                        trailingIcon = {
                            if (brandQuery.isNotEmpty()) IconButton(
                                onClick = {
                                    brandQuery = ""
                                    selectedBrand = ""
                                    resetFromBrand()
                                }
                            ) {
                                Icon(Icons.Default.Close, "Очистить")
                            }
                        },
                        colors = addCarTextFieldColors(),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = ThemeManager.textPrimary)
                    )
                    if (showBrands && filteredBrands.isNotEmpty()) {
                        SuggestionsDropdown(
                            items = filteredBrands,
                            selectedItem = selectedBrand,
                            onItemClick = { brand ->
                                selectedBrand = brand
                                brandQuery = brand
                                showBrands = false
                                resetFromBrand()
                            }
                        )
                    }
                }
            }

            // ===== 2. МОДЕЛЬ =====
            if (selectedBrand.isNotEmpty()) {
                item {
                    Column {
                        OutlinedTextField(
                            value = modelQuery,
                            onValueChange = {
                                modelQuery = it
                                showModels = true
                                if (it.isBlank()) {
                                    selectedModel = ""
                                    resetFromModel()
                                }
                            },
                            label = { Text("Модель *") },
                            placeholder = { Text("Выберите или начните вводить") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { showModels = it.isFocused },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Search, "Поиск") },
                            trailingIcon = {
                                if (modelQuery.isNotEmpty()) IconButton(
                                    onClick = {
                                        modelQuery = ""
                                        selectedModel = ""
                                        resetFromModel()
                                    }
                                ) {
                                    Icon(Icons.Default.Close, "Очистить")
                                }
                            },
                            colors = addCarTextFieldColors(),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = ThemeManager.textPrimary)
                        )
                        if (showModels && filteredModels.isNotEmpty()) {
                            SuggestionsDropdown(
                                items = filteredModels,
                                selectedItem = selectedModel,
                                onItemClick = { model ->
                                    selectedModel = model
                                    modelQuery = model
                                    showModels = false
                                    resetFromModel()
                                }
                            )
                        }
                    }
                }
            }

            // ===== 3. ПОКОЛЕНИЕ =====
            if (selectedModel.isNotEmpty() && availableGenerations.isNotEmpty()) {
                item {
                    Column {
                        OutlinedTextField(
                            value = generationQuery,
                            onValueChange = {
                                generationQuery = it
                                showGenerationSuggestions = true
                                if (it.isBlank()) {
                                    selectedGeneration = ""
                                    selectedTrim = null
                                    trimSearchQuery = ""
                                }
                            },
                            label = { Text("Поколение") },
                            placeholder = { Text("Выберите или начните вводить") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { showGenerationSuggestions = it.isFocused },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Search, "Поиск") },
                            trailingIcon = {
                                if (generationQuery.isNotEmpty()) IconButton(
                                    onClick = {
                                        generationQuery = ""
                                        selectedGeneration = ""
                                        selectedTrim = null
                                        trimSearchQuery = ""
                                    }
                                ) {
                                    Icon(Icons.Default.Close, "Очистить")
                                }
                            },
                            colors = addCarTextFieldColors(),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = ThemeManager.textPrimary)
                        )
                        if (showGenerationSuggestions && filteredGenerations.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                                    items(filteredGenerations) { (name, years) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedGeneration = name
                                                    generationQuery = name
                                                    showGenerationSuggestions = false
                                                    selectedTrim = null
                                                    trimSearchQuery = ""
                                                }
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(name, color = ThemeManager.textPrimary)
                                                Text(
                                                    years,
                                                    color = ThemeManager.textSecondary,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                            if (selectedGeneration == name) Text(
                                                "✓",
                                                color = ThemeManager.accentColor
                                            )
                                        }
                                        HorizontalDivider(color = ThemeManager.accentColor.copy(alpha = 0.1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ===== 4. КОМПЛЕКТАЦИЯ (КНОПКА) =====
            if (selectedGeneration.isNotEmpty() && availableTrims.isNotEmpty()) {
                item(key = "trim_field") {
                    Column {
                        Text(
                            "Комплектация",
                            style = MaterialTheme.typography.labelMedium,
                            color = ThemeManager.textSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Button(
                            onClick = { showTrimDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ThemeManager.accentColor.copy(alpha = 0.15f),
                                contentColor = ThemeManager.textPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedTrim?.let {
                                        "${it.name} (${it.engine.volume}л, ${it.powerHp} л.с.)"
                                    } ?: "Нажмите для выбора комплектации",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (selectedTrim != null) ThemeManager.textPrimary else ThemeManager.textHint
                                )

                                if (selectedTrim != null) {
                                    IconButton(
                                        onClick = {
                                            selectedTrim = null
                                            trimSearchQuery = ""
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            "Очистить",
                                            tint = ThemeManager.accentColor
                                        )
                                    }
                                } else {
                                    Text("▼", style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }
                    }
                }

                if (selectedTrim != null) {
                    item(key = "trim_details_${selectedTrim!!.name}") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = ThemeManager.accentColor.copy(alpha = 0.08f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "🔧 Детали комплектации",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = ThemeManager.accentColor
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Двигатель: ${selectedTrim!!.engine.type} ${selectedTrim!!.engine.volume}л (${selectedTrim!!.engine.code})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ThemeManager.textSecondary
                                )
                                Text(
                                    "КПП: ${selectedTrim!!.transmission.type} ${selectedTrim!!.transmission.gears}ст. ${selectedTrim!!.transmission.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ThemeManager.textSecondary
                                )
                                Text(
                                    "Привод: ${selectedTrim!!.driveType} | Топливо: ${selectedTrim!!.fuelType}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ThemeManager.textSecondary
                                )
                                Text(
                                    "Мощность: ${selectedTrim!!.powerHp} л.с. | 0-100: ${selectedTrim!!.acceleration}с",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ThemeManager.textSecondary
                                )
                            }
                        }
                    }
                }
            }

            // ===== ГОД И НОМЕР =====
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it },
                        label = { Text("Год") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = addCarTextFieldColors(),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = ThemeManager.textPrimary)
                    )
                    OutlinedTextField(
                        value = plate,
                        onValueChange = { plate = it.uppercase() },
                        label = { Text("Госномер") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = addCarTextFieldColors(),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = ThemeManager.textPrimary)
                    )
                }
            }

            // ===== ПРОБЕГ =====
            item {
                OutlinedTextField(
                    value = mileage,
                    onValueChange = { mileage = it },
                    label = { Text("Пробег (км)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = addCarTextFieldColors(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = ThemeManager.textPrimary)
                )
            }

            // ===== КНОПКА СОХРАНЕНИЯ =====
            item {
                Button(
                    onClick = {
                        if (selectedBrand.isNotBlank() && selectedModel.isNotBlank()) {
                            val fuelType = selectedTrim?.fuelType ?: "Бензин"

                            val photoPath = if (carPhotoUri != null) {
                                ImageUtils.saveImageToInternalStorage(context, carPhotoUri!!)
                            } else null

                            if (isEditMode && existingCar != null) {
                                viewModel.updateCar(
                                    existingCar.copy(
                                        brand = selectedBrand,
                                        model = selectedModel,
                                        year = year.toIntOrNull() ?: existingCar.year,
                                        plate = plate,
                                        mileage = mileage.toIntOrNull() ?: existingCar.mileage,
                                        fuelType = fuelType,
                                        photoUrl = photoPath ?: existingCar.photoUrl
                                    )
                                )
                                onBack()
                            } else {
                                viewModel.addCar(
                                    brand = selectedBrand,
                                    model = selectedModel,
                                    year = year.toIntOrNull() ?: 0,
                                    plate = plate,
                                    mileage = mileage.toIntOrNull() ?: 0,
                                    fuelType = fuelType,
                                    photoUrl = photoPath
                                )
                                onCarAdded(viewModel.cars.value.lastOrNull()?.id ?: "")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = selectedBrand.isNotBlank() && selectedModel.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemeManager.accentColor.copy(alpha = 0.15f),
                        contentColor = ThemeManager.accentColor,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = ThemeManager.accentColor.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        if (isEditMode) "💾 Сохранить изменения" else "💾 Сохранить",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            item {
                Text(
                    "* Поля обязательные для заполнения",
                    style = MaterialTheme.typography.bodySmall,
                    color = ThemeManager.textHint,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    // ===== ДИАЛОГ ВЫБОРА ФОТО =====
    if (showPhotoDialog) {
        Dialog(onDismissRequest = { showPhotoDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (carPhotoUri != null) 280.dp else 200.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "📸 Фото автомобиля",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ThemeManager.textPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                showPhotoDialog = false
                                isPhotoLoading = true
                                photoPickerLauncher.launch("image/*")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ThemeManager.accentColor.copy(alpha = 0.15f),
                                contentColor = ThemeManager.accentColor
                            )
                        ) {
                            Row(horizontalArrangement = Arrangement.Center) {
                                Icon(Icons.Default.PhotoLibrary, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Выбрать из галереи")
                            }
                        }

                        if (carPhotoUri != null) {
                            Button(
                                onClick = {
                                    showPhotoDialog = false
                                    originalPhotoUri?.let { uri ->
                                        startCropWithLauncher(uri)
                                    } ?: run {
                                        Toast.makeText(context, "Выберите фото сначала", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50).copy(alpha = 0.15f),
                                    contentColor = Color(0xFF4CAF50)
                                )
                            ) {
                                Row(horizontalArrangement = Arrangement.Center) {
                                    Icon(Icons.Default.Crop, null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Обрезать фото")
                                }
                            }
                        }

                        if (carPhotoUri != null) {
                            Button(
                                onClick = {
                                    carPhotoUri = null
                                    originalPhotoUri = null
                                    showPhotoDialog = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFB71C1C).copy(alpha = 0.15f),
                                    contentColor = Color(0xFFB71C1C)
                                )
                            ) {
                                Row(horizontalArrangement = Arrangement.Center) {
                                    Icon(Icons.Default.Delete, null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Удалить фото")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = { showPhotoDialog = false },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = ThemeManager.textSecondary
                        )
                    ) {
                        Text("Отмена")
                    }
                }
            }
        }
    }

    // ===== ДИАЛОГ КОМПЛЕКТАЦИИ =====
    if (showTrimDialog) {
        Dialog(onDismissRequest = {
            showTrimDialog = false
            trimSearchQuery = ""
        }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "⚙️ Выбор комплектации",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ThemeManager.textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = trimSearchQuery,
                        onValueChange = { trimSearchQuery = it },
                        placeholder = { Text("Поиск по названию или коду двигателя") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        singleLine = true,
                        colors = addCarTextFieldColors(),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = ThemeManager.textPrimary),
                        leadingIcon = { Icon(Icons.Default.Search, "Поиск") },
                        trailingIcon = {
                            if (trimSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { trimSearchQuery = "" }) {
                                    Icon(Icons.Default.Close, "Очистить")
                                }
                            }
                        }
                    )

                    HorizontalDivider(color = ThemeManager.accentColor.copy(alpha = 0.2f))

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredTrims) { trim ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        trim.name,
                                        color = ThemeManager.textPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                supportingContent = {
                                    Column {
                                        Text(
                                            "${trim.engine.type} ${trim.engine.volume}л (${trim.engine.code}) • ${trim.powerHp} л.с.",
                                            color = ThemeManager.textSecondary,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            "${trim.transmission.type} ${trim.transmission.gears}ст. • ${trim.driveType} • ${trim.fuelType}",
                                            color = ThemeManager.textSecondary,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedTrim = trim
                                        trimSearchQuery = ""
                                        showTrimDialog = false
                                    }
                            )
                            HorizontalDivider(color = ThemeManager.accentColor.copy(alpha = 0.1f))
                        }

                        if (filteredTrims.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Ничего не найдено",
                                        color = ThemeManager.textHint,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ===== ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ =====

@Composable
private fun SuggestionsDropdown(
    items: List<String>,
    selectedItem: String,
    onItemClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
            items(items) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item, color = ThemeManager.textPrimary)
                    if (item == selectedItem) Text("✓", color = ThemeManager.accentColor)
                }
                HorizontalDivider(color = ThemeManager.accentColor.copy(alpha = 0.1f))
            }
        }
    }
}

@Composable
private fun addCarTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ThemeManager.accentColor,
    unfocusedBorderColor = ThemeManager.accentColor.copy(alpha = 0.3f),
    focusedLabelColor = ThemeManager.accentColor,
    unfocusedLabelColor = ThemeManager.textSecondary,
    cursorColor = ThemeManager.accentColor,
    focusedTextColor = ThemeManager.textPrimary,
    unfocusedTextColor = ThemeManager.textPrimary
)