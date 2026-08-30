package com.autojournal.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.app.Application
import android.util.Log
import com.autojournal.data.model.Car
import com.autojournal.data.model.Expense
import com.autojournal.data.model.RepairTask
import com.autojournal.data.repository.AppRepository
import com.autojournal.data.preferences.SettingsPreferences
import com.autojournal.utils.WidgetDataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: AppRepository,
    private val prefs: SettingsPreferences,
    private val application: Application
) : ViewModel() {

    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    val cars: StateFlow<List<Car>> = _cars.asStateFlow()

    private val _selectedCarId = MutableStateFlow<String?>(null)
    val selectedCarId: StateFlow<String?> = _selectedCarId.asStateFlow()

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _pendingTasks = MutableStateFlow<List<RepairTask>>(emptyList())
    val pendingTasks: StateFlow<List<RepairTask>> = _pendingTasks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val deviceId: String = prefs.getDeviceId()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("DashboardViewModel", "loadData() вызван")

            repository.getCars(deviceId).collectLatest { carList ->
                _cars.value = carList
                Log.d("DashboardViewModel", "Загружено авто: ${carList.size}")

                if (carList.isNotEmpty() && _selectedCarId.value == null) {
                    _selectedCarId.value = carList.first().id
                    loadExpensesAndTasks(carList.first().id)
                } else if (carList.isNotEmpty() && _selectedCarId.value != null) {
                    loadExpensesAndTasks(_selectedCarId.value!!)
                }
                _isLoading.value = false
            }
        }
    }

    fun selectCar(carId: String) {
        _selectedCarId.value = carId
        loadExpensesAndTasks(carId)
        // Обновляем виджет при выборе авто
        updateWidgetForCar(carId)
    }

    private fun loadExpensesAndTasks(carId: String) {
        viewModelScope.launch {
            Log.d("DashboardViewModel", "loadExpensesAndTasks для авто: $carId")

            repository.getExpenses(carId).collectLatest { expenseList ->
                _expenses.value = expenseList
                Log.d("DashboardViewModel", "Загружено трат: ${expenseList.size}")
                // Обновляем виджет после загрузки расходов
                updateWidgetForCar(carId)
            }
        }

        viewModelScope.launch {
            repository.getPendingTasks(carId).collectLatest { taskList ->
                _pendingTasks.value = taskList
                Log.d("DashboardViewModel", "Загружено задач: ${taskList.size}")
            }
        }
    }

    private fun updateWidgetForCar(carId: String) {
        viewModelScope.launch {
            val car = _cars.value.find { it.id == carId }
            val expenses = _expenses.value
            car?.let {
                WidgetDataManager.updateWidgetData(
                    context = application.applicationContext,
                    car = it,
                    expenses = expenses
                )
            }
        }
    }

    fun getSelectedCar(): Car? = _cars.value.find { it.id == _selectedCarId.value }

    fun addCar(
        brand: String,
        model: String,
        year: Int,
        plate: String,
        mileage: Int,
        fuelType: String = "Бензин",
        photoUrl: String? = null
    ) {
        viewModelScope.launch {
            val car = Car(
                deviceId = deviceId,
                brand = brand,
                model = model,
                year = year,
                plate = plate,
                mileage = mileage,
                fuelType = fuelType,
                photoUrl = photoUrl
            )
            repository.insertCar(car)
            loadData()
            // Обновляем виджет после добавления авто
            WidgetDataManager.updateWidgetData(
                context = application.applicationContext,
                car = car,
                expenses = emptyList()
            )
        }
    }

    fun deleteCar(car: Car) {
        viewModelScope.launch {
            repository.deleteCar(car)
            repository.deleteAllExpenses(car.id)
            loadData()
            // Обновляем виджет
            WidgetDataManager.updateWidget(application.applicationContext)
        }
    }

    fun updateCar(car: Car) {
        viewModelScope.launch {
            repository.updateCar(car)
            loadData()
            // Обновляем виджет
            updateWidgetForCar(car.id)
        }
    }

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            Log.d("DashboardViewModel", "Добавление траты: ${expense.amount}")
            repository.insertExpense(expense)
            loadData()
            // Обновляем виджет после добавления расхода
            if (expense.carId.isNotEmpty()) {
                updateWidgetForCar(expense.carId)
            }
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            loadData()
            // Обновляем виджет
            if (expense.carId.isNotEmpty()) {
                updateWidgetForCar(expense.carId)
            }
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            repository.updateExpense(expense)
            loadData()
            if (expense.carId.isNotEmpty()) {
                updateWidgetForCar(expense.carId)
            }
        }
    }

    fun addTask(task: RepairTask) {
        viewModelScope.launch {
            Log.d("DashboardViewModel", "Добавление задачи: ${task.title}")
            repository.insertTask(task)
            loadData()
        }
    }

    fun completeTask(taskId: String) {
        viewModelScope.launch {
            Log.d("DashboardViewModel", "Выполнение задачи: $taskId")
            repository.completeTask(taskId)
            loadData()
        }
    }

    fun deleteTask(task: RepairTask) {
        viewModelScope.launch {
            Log.d("DashboardViewModel", "Удаление задачи: ${task.id}")
            repository.deleteTask(task)
            loadData()
        }
    }

    fun updateTask(task: RepairTask) {
        viewModelScope.launch {
            repository.updateTask(task)
            loadData()
        }
    }

    fun clearCompletedTasks(carId: String) {
        viewModelScope.launch {
            repository.clearCompleted(carId)
            loadData()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.deleteAllCars(deviceId)
            _cars.value = emptyList()
            _expenses.value = emptyList()
            _pendingTasks.value = emptyList()
            _selectedCarId.value = null
            // Очищаем данные виджета
            prefs.clearWidgetData()
            WidgetDataManager.updateWidget(application.applicationContext)
        }
    }

    fun completeRepairTask(taskId: String, amount: Double, mileage: Int) {
        viewModelScope.launch {
            Log.d("DashboardViewModel", "completeRepairTask: $taskId, amount: $amount, mileage: $mileage")

            val task = repository.getTaskById(taskId)
            task?.let {
                val updatedTask = it.copy(completed = true)
                repository.updateTask(updatedTask)

                val expense = Expense(
                    carId = it.carId,
                    category = "Ремонт",
                    amount = amount,
                    mileage = mileage,
                    description = it.title,
                    createdAt = System.currentTimeMillis()
                )
                repository.insertExpense(expense)

                Log.d("DashboardViewModel", "Ремонт завершён и добавлен в расходы")
                loadData()
                if (it.carId.isNotEmpty()) {
                    updateWidgetForCar(it.carId)
                }
            } ?: run {
                Log.e("DashboardViewModel", "Задача не найдена: $taskId")
            }
        }
    }

    fun hasData(): Boolean = _cars.value.isNotEmpty()
    fun getTotalExpenses(): Double = _expenses.value.sumOf { it.amount }
    fun getTotalTasks(): Int = _pendingTasks.value.size
}