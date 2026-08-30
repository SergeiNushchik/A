package com.autojournal.data

object FuelPrices {

    // Цены на топливо в РБ
    val prices = mapOf(
        "АИ-92" to 2.60,
        "АИ-95" to 2.70,
        "АИ-98" to 2.92,
        "Дизельное" to 2.70,
        "Газ" to 1.80,      // ← ДОБАВЛЕНО
        "Электро" to 0.50   // ← ДОБАВЛЕНО (условно)
    )

    // Доступные виды топлива
    val fuelTypes = listOf("АИ-92", "АИ-95", "АИ-98", "Дизельное", "Газ", "Электро")

    // Маппинг типа авто → доступные виды топлива
    fun getFuelTypesForCar(carFuelType: String): List<String> {
        return when (carFuelType) {
            "Бензин" -> listOf("АИ-92", "АИ-95", "АИ-98")
            "Дизель" -> listOf("Дизельное")
            "Газ" -> listOf("Газ")
            "Электро" -> listOf("Электро")
            else -> listOf("АИ-92", "АИ-95", "АИ-98")  // по умолчанию бензин
        }
    }
}