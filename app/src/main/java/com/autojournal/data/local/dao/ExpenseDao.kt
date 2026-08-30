package com.autojournal.data.local.dao

import androidx.room.*
import com.autojournal.data.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE carId = :carId ORDER BY createdAt DESC")
    fun getExpenses(carId: String): Flow<List<Expense>>

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Query("DELETE FROM expenses WHERE carId = :carId")
    suspend fun deleteAll(carId: String)

    @Query("SELECT SUM(amount) FROM expenses WHERE carId = :carId AND category = :category")
    suspend fun getTotalByCategory(carId: String, category: String): Double?
}