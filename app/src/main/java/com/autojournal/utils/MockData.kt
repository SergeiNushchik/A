package com.autojournal.utils

import com.autojournal.data.model.Car
import com.autojournal.data.model.Expense
import com.autojournal.data.model.RepairTask
import java.util.UUID

object MockData {

    private val deviceId = "device-123"

    fun getMockCars(): List<Car> {
        return listOf(
            Car(
                id = UUID.randomUUID().toString(),
                deviceId = deviceId,
                brand = "Toyota",
                model = "Camry",
                year = 2020,
                plate = "АА1234",
                mileage = 45230,
                lastOilChangeKm = 35000,
                insuranceExpiry = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000 // через 30 дней
            ),
            Car(
                id = UUID.randomUUID().toString(),
                deviceId = deviceId,
                brand = "BMW",
                model = "X5",
                year = 2022,
                plate = "ВВ5678",
                mileage = 12000,
                lastOilChangeKm = 5000,
                insuranceExpiry = System.currentTimeMillis() + 200L * 24 * 60 * 60 * 1000 // через 200 дней
            )
        )
    }

    fun getMockExpenses(carId: String): List<Expense> {
        return listOf(
            Expense(
                id = UUID.randomUUID().toString(),
                carId = carId,
                category = "FUEL",
                amount = 120.0,
                mileage = 45230,
                description = "Заправка АИ-95",
                createdAt = System.currentTimeMillis() - 2L * 24 * 60 * 60 * 1000
            ),
            Expense(
                id = UUID.randomUUID().toString(),
                carId = carId,
                category = "REPAIR",
                amount = 3500.0,
                mileage = 44000,
                description = "Замена масла и фильтров",
                createdAt = System.currentTimeMillis() - 14L * 24 * 60 * 60 * 1000
            ),
            Expense(
                id = UUID.randomUUID().toString(),
                carId = carId,
                category = "INSURANCE",
                amount = 850.0,
                mileage = 43000,
                description = "ОСАГО",
                createdAt = System.currentTimeMillis() - 60L * 24 * 60 * 60 * 1000
            ),
            Expense(
                id = UUID.randomUUID().toString(),
                carId = carId,
                category = "WASH",
                amount = 25.0,
                mileage = 44500,
                description = "Мойка",
                createdAt = System.currentTimeMillis() - 5L * 24 * 60 * 60 * 1000
            )
        )
    }

    fun getMockTasks(carId: String): List<RepairTask> {
        return listOf(
            RepairTask(
                id = UUID.randomUUID().toString(),
                carId = carId,
                title = "Замена масла",
                priority = "YELLOW",
                dueDate = System.currentTimeMillis() + 10L * 24 * 60 * 60 * 1000,
                dueMileage = 50000
            ),
            RepairTask(
                id = UUID.randomUUID().toString(),
                carId = carId,
                title = "Продлить страховку ОСАГО",
                priority = "RED",
                dueDate = System.currentTimeMillis() + 5L * 24 * 60 * 60 * 1000,
                dueMileage = 0
            ),
            RepairTask(
                id = UUID.randomUUID().toString(),
                carId = carId,
                title = "Заменить тормозные колодки",
                priority = "GREEN",
                dueDate = System.currentTimeMillis() + 60L * 24 * 60 * 60 * 1000,
                dueMileage = 52000
            )
        )
    }
}