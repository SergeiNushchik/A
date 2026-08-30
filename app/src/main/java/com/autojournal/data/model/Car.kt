package com.autojournal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "cars")
data class Car(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val deviceId: String = "",
    val tenantId: String? = null,
    val vin: String = "",
    val brand: String = "",
    val model: String = "",
    val year: Int = 0,
    val plate: String = "",
    val mileage: Int = 0,
    val lastOilChangeKm: Int = 0,
    val insuranceExpiry: Long = 0,
    val obdAdapterMac: String = "",
    val photoUrl: String? = null,  // ← ЭТО ПОЛЕ ДЛЯ ФОТО
    val fuelType: String = "Бензин",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)