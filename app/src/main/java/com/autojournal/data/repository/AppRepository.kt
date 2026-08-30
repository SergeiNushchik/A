package com.autojournal.data.repository

import com.autojournal.data.local.dao.CarDao
import com.autojournal.data.local.dao.ExpenseDao
import com.autojournal.data.local.dao.RepairTaskDao
import com.autojournal.data.model.Car
import com.autojournal.data.model.Expense
import com.autojournal.data.model.RepairTask
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val carDao: CarDao,
    private val expenseDao: ExpenseDao,
    private val repairTaskDao: RepairTaskDao
) {
    // ===== АВТОМОБИЛИ =====
    fun getCars(deviceId: String): Flow<List<Car>> = carDao.getCars(deviceId)
    suspend fun getCarById(carId: String): Car? = carDao.getCarById(carId)
    suspend fun insertCar(car: Car) = carDao.insertCar(car)
    suspend fun updateCar(car: Car) = carDao.updateCar(car)
    suspend fun deleteCar(car: Car) = carDao.deleteCar(car)
    suspend fun deleteAllCars(deviceId: String) = carDao.deleteAll(deviceId)

    // ===== ТРАТЫ =====
    fun getExpenses(carId: String): Flow<List<Expense>> = expenseDao.getExpenses(carId)
    suspend fun insertExpense(expense: Expense) = expenseDao.insertExpense(expense)
    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)
    suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)
    suspend fun deleteAllExpenses(carId: String) = expenseDao.deleteAll(carId)
    suspend fun getTotalByCategory(carId: String, category: String): Double? =
        expenseDao.getTotalByCategory(carId, category)

    // ===== ЗАДАЧИ РЕМОНТА =====
    fun getPendingTasks(carId: String): Flow<List<RepairTask>> = repairTaskDao.getPendingTasks(carId)
    fun getAllTasks(carId: String): Flow<List<RepairTask>> = repairTaskDao.getAllTasks(carId)
    fun getCompletedTasks(carId: String): Flow<List<RepairTask>> = repairTaskDao.getCompletedTasks(carId)
    suspend fun insertTask(task: RepairTask) = repairTaskDao.insertTask(task)
    suspend fun updateTask(task: RepairTask) = repairTaskDao.updateTask(task)
    suspend fun deleteTask(task: RepairTask) = repairTaskDao.deleteTask(task)
    suspend fun completeTask(taskId: String) = repairTaskDao.completeTask(taskId)
    suspend fun clearCompleted(carId: String) = repairTaskDao.clearCompleted(carId)
    suspend fun getTaskById(taskId: String): RepairTask? {
        return repairTaskDao.getTaskById(taskId)
    }
}