package com.autojournal.data.local.dao

import androidx.room.*
import com.autojournal.data.model.RepairTask
import kotlinx.coroutines.flow.Flow

@Dao
interface RepairTaskDao {
    @Query("SELECT * FROM repair_tasks WHERE carId = :carId AND completed = 0 ORDER BY dueDate ASC")
    fun getPendingTasks(carId: String): Flow<List<RepairTask>>

    @Query("SELECT * FROM repair_tasks WHERE carId = :carId ORDER BY dueDate ASC")
    fun getAllTasks(carId: String): Flow<List<RepairTask>>

    @Query("SELECT * FROM repair_tasks WHERE carId = :carId AND completed = 1 ORDER BY dueDate DESC")
    fun getCompletedTasks(carId: String): Flow<List<RepairTask>>
    @Query("SELECT * FROM repair_tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): RepairTask?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: RepairTask)

    @Update
    suspend fun updateTask(task: RepairTask)

    @Delete
    suspend fun deleteTask(task: RepairTask)

    @Query("UPDATE repair_tasks SET completed = 1 WHERE id = :taskId")
    suspend fun completeTask(taskId: String)

    @Query("DELETE FROM repair_tasks WHERE carId = :carId AND completed = 1")
    suspend fun clearCompleted(carId: String)
}