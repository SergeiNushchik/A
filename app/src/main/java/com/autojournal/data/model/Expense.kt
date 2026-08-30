package com.autojournal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val carId: String,
    val category: String,
    val amount: Double,
    val mileage: Int,
    val description: String = "",
    val photoUrls: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class ExpenseCategory {
    FUEL, REPAIR, INSURANCE, WASH, TIRES, OTHER
}