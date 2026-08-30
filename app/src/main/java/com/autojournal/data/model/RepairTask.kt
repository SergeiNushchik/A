package com.autojournal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "repair_tasks")
data class RepairTask(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val carId: String,
    val title: String,
    val priority: String,
    val dueDate: Long,
    val dueMileage: Int = 0,
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)