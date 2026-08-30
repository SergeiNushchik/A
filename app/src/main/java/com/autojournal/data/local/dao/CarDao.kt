package com.autojournal.data.local.dao

import androidx.room.*
import com.autojournal.data.model.Car
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDao {
    @Query("SELECT * FROM cars WHERE deviceId = :deviceId ORDER BY createdAt DESC")
    fun getCars(deviceId: String): Flow<List<Car>>

    @Query("SELECT * FROM cars WHERE id = :carId")
    suspend fun getCarById(carId: String): Car?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: Car)

    @Update
    suspend fun updateCar(car: Car)

    @Delete
    suspend fun deleteCar(car: Car)

    @Query("DELETE FROM cars WHERE deviceId = :deviceId")
    suspend fun deleteAll(deviceId: String)
}