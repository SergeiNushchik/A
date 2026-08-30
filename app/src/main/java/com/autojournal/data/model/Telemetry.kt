package com.autojournal.data.model

data class Telemetry(
    val carId: String,
    val rpm: Int = 0,
    val speed: Int = 0,
    val coolantTemp: Float = 0f,
    val voltage: Float = 0f,
    val fuelRate: Float = 0f,
    val engineLoad: Int = 0,
    val throttlePosition: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)