package com.autojournal.ble

data class DTC(
    val code: String,
    val description: String,
    val severity: String = "Средняя"
)

data class TelemetryData(
    // Основные параметры
    val rpm: Float = 0f,
    val speed: Float = 0f,
    val coolantTemp: Float = 0f,
    val voltage: Float = 0f,
    val mileage: Int = 0,
    val fuelLevel: Float = 0f,
    val dtcCodes: List<DTC> = emptyList(),

    // ===== НОВЫЕ ПОЛЯ =====
    val engineLoad: Float = 0f,          // Нагрузка двигателя %
    val intakeTemp: Float = 0f,          // Температура входящего воздуха °C
    val throttlePos: Float = 0f,         // Положение дроссельной заслонки %
    val mafFlow: Float = 0f,             // Массовый расход воздуха г/с
    val fuelPressure: Float = 0f,        // Давление топлива кПа
    val timingAdvance: Float = 0f,       // Угол опережения зажигания °
    val oxygenSensor1: Float = 0f,       // Кислородный датчик 1 В
    val oxygenSensor2: Float = 0f,       // Кислородный датчик 2 В
    val engineRuntime: Int = 0,          // Время работы двигателя с
    val distanceWithMIL: Int = 0,        // Пробег с момента ошибки км

    // Вспомогательные поля
    val timestamp: Long = System.currentTimeMillis(),
    val isConnected: Boolean = false
)