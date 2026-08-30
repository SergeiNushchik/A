package com.autojournal.data.model

import kotlinx.serialization.Serializable

/**
 * Полная иерархия автомобиля:
 * Марка → Модель → Поколение → Комплектация
 */

@Serializable
data class CarBrand(
    val name: String,                    // "BMW"
    val models: List<CarModel>
)

@Serializable
data class CarModel(
    val name: String,                    // "3 серия"
    val generations: List<CarGeneration>
)

@Serializable
data class CarGeneration(
    val name: String,                    // "E90/E91/E92/E93"
    val years: String,                   // "2005-2013"
    val bodyTypes: List<String>,         // ["Седан", "Универсал", "Купе", "Кабриолет"]
    val trims: List<CarTrim>
)

@Serializable
data class CarTrim(
    val name: String,                    // "320d"
    val engine: EngineSpec,
    val transmission: TransmissionSpec,
    val driveType: String = "Задний",    // "Передний", "Задний", "Полный"
    val fuelType: String = "Дизель",     // "Бензин", "Дизель", "Электро", "Гибрид"
    val powerHp: Int = 0,                // Мощность в л.с.
    val acceleration: Double = 0.0       // 0-100 км/ч, сек
)

@Serializable
data class EngineSpec(
    val type: String,                    // "Рядный 4-цилиндровый"
    val volume: Double,                  // 2.0
    val code: String = "",               // "N47D20"
    val turbo: Boolean = false,
    val cylinders: Int = 4,
    val valvesPerCylinder: Int = 4
)

@Serializable
data class TransmissionSpec(
    val type: String,                    // "АКПП", "МКПП", "Вариатор", "Робот"
    val gears: Int = 6,                  // Количество передач
    val name: String = ""                // "ZF 6HP", "Getrag" и т.д.
)