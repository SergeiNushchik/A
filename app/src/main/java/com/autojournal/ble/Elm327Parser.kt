package com.autojournal.ble

import android.util.Log

class Elm327Parser {
    companion object {
        private const val TAG = "Elm327Parser"
    }

    fun parseResponse(response: String): TelemetryData? {
        val cleanResponse = response.trim()
        Log.d(TAG, "📥 Парсинг: $cleanResponse")

        // ===== ПРОВЕРКА НА ОШИБКИ =====
        if (cleanResponse.startsWith("7F")) {
            Log.w(TAG, "⚠️ Получен ответ об ошибке: $cleanResponse")
            return null
        }

        if (cleanResponse.contains("NO DATA", ignoreCase = true) ||
            cleanResponse.contains("UNABLE TO CONNECT", ignoreCase = true) ||
            cleanResponse.contains("SEARCHING", ignoreCase = true) ||
            cleanResponse.contains("BUS INIT", ignoreCase = true)) {
            Log.w(TAG, "⚠️ Получен служебный ответ: $cleanResponse")
            return null
        }

        // ===== ПАРСИНГ =====
        return when {
            // RPM (010C)
            cleanResponse.contains("010C") || cleanResponse.contains("410C") || cleanResponse.contains("41 0C") -> {
                parseRPM(cleanResponse)
            }
            // Speed (010D)
            cleanResponse.contains("010D") || cleanResponse.contains("410D") || cleanResponse.contains("41 0D") -> {
                parseSpeed(cleanResponse)
            }
            // Coolant Temp (0105)
            cleanResponse.contains("0105") || cleanResponse.contains("4105") || cleanResponse.contains("41 05") -> {
                parseCoolantTemp(cleanResponse)
            }
            // Voltage (ATRV)
            cleanResponse.contains("ATRV") || cleanResponse.matches(Regex("\\d+\\.\\d+V")) -> {
                parseVoltage(cleanResponse)
            }
            // Engine Load (0104)
            cleanResponse.contains("0104") || cleanResponse.contains("4104") || cleanResponse.contains("41 04") -> {
                parseEngineLoad(cleanResponse)
            }
            // Intake Temp (010F)
            cleanResponse.contains("010F") || cleanResponse.contains("410F") || cleanResponse.contains("41 0F") -> {
                parseIntakeTemp(cleanResponse)
            }
            // Throttle Position (0111)
            cleanResponse.contains("0111") || cleanResponse.contains("4111") || cleanResponse.contains("41 11") -> {
                parseThrottlePos(cleanResponse)
            }
            // Fuel Level (012F)
            cleanResponse.contains("012F") || cleanResponse.contains("412F") || cleanResponse.contains("41 2F") -> {
                parseFuelLevel(cleanResponse)
            }
            // MAF (0110)
            cleanResponse.contains("0110") || cleanResponse.contains("4110") || cleanResponse.contains("41 10") -> {
                parseMAF(cleanResponse)
            }
            // Fuel Pressure (010A)
            cleanResponse.contains("010A") || cleanResponse.contains("410A") || cleanResponse.contains("41 0A") -> {
                parseFuelPressure(cleanResponse)
            }
            // Timing Advance (010E)
            cleanResponse.contains("010E") || cleanResponse.contains("410E") || cleanResponse.contains("41 0E") -> {
                parseTimingAdvance(cleanResponse)
            }
            // ODOMETER (0146)
            cleanResponse.contains("0146") || cleanResponse.contains("4146") || cleanResponse.contains("41 46") -> {
                parseMileage(cleanResponse)
            }
            // DTC (43)
            cleanResponse.contains("43") && cleanResponse.length > 4 -> {
                parseDTC(cleanResponse)
            }
            else -> {
                Log.d(TAG, "⚠️ Неизвестный тип ответа: $cleanResponse")
                null
            }
        }
    }

    // ===== ПАРСИНГ RPM (010C) =====
    private fun parseRPM(response: String): TelemetryData? {
        val dataBytes = extractDataBytes(response, "0C")
        Log.d(TAG, "RPM Data: ${dataBytes.joinToString(" ")}")

        if (dataBytes.isEmpty()) {
            Log.w(TAG, "⚠️ Нет данных для RPM")
            return null
        }

        if (dataBytes.size >= 2) {
            try {
                val rpm = ((dataBytes[0].toInt() shl 8) or dataBytes[1].toInt()) / 4
                if (rpm >= 0 && rpm < 20000) {
                    Log.d(TAG, "✅ RPM: $rpm")
                    return TelemetryData(rpm = rpm.toFloat())
                } else {
                    Log.w(TAG, "⚠️ Невалидный RPM: $rpm")
                    return null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка парсинга RPM", e)
                return null
            }
        }
        return null
    }

    // ===== ПАРСИНГ СКОРОСТИ (010D) =====
    private fun parseSpeed(response: String): TelemetryData? {
        val dataBytes = extractDataBytes(response, "0D")
        Log.d(TAG, "Speed Data: ${dataBytes.joinToString(" ")}")

        if (dataBytes.isEmpty()) {
            Log.w(TAG, "⚠️ Нет данных для Speed")
            return null
        }

        if (dataBytes.isNotEmpty()) {
            try {
                val speed = dataBytes[0].toInt()
                if (speed >= 0 && speed < 300) {
                    Log.d(TAG, "✅ Speed: $speed км/ч")
                    return TelemetryData(speed = speed.toFloat())
                } else {
                    Log.w(TAG, "⚠️ Невалидная скорость: $speed")
                    return null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка парсинга Speed", e)
                return null
            }
        }
        return null
    }

    // ===== ПАРСИНГ ТЕМПЕРАТУРЫ ОХЛАЖДЕНИЯ (0105) =====
    private fun parseCoolantTemp(response: String): TelemetryData? {
        val dataBytes = extractDataBytes(response, "05")
        Log.d(TAG, "Coolant Temp Data: ${dataBytes.joinToString(" ")}")

        if (dataBytes.isEmpty()) {
            Log.w(TAG, "⚠️ Нет данных для температуры")
            return null
        }

        if (dataBytes.isNotEmpty()) {
            try {
                val raw = dataBytes[0].toInt()
                val temp = raw - 40

                if (temp > 10 && temp < 300) {
                    Log.d(TAG, "✅ Coolant Temp: $temp°C")
                    return TelemetryData(coolantTemp = temp.toFloat())
                } else {
                    Log.w(TAG, "⚠️ Невалидная температура: $temp°C (raw=$raw)")
                    return null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка парсинга Coolant Temp", e)
                return null
            }
        }
        return null
    }

    // ===== ПАРСИНГ НАПРЯЖЕНИЯ (ATRV) =====
    private fun parseVoltage(response: String): TelemetryData? {
        try {
            val voltageMatch = Regex("(\\d+\\.?\\d*)").find(response)
            voltageMatch?.let {
                val voltage = it.groupValues[1].toFloat()
                if (voltage in 6.0f..18.0f) {
                    Log.d(TAG, "✅ Voltage: $voltage V")
                    return TelemetryData(voltage = voltage)
                } else {
                    Log.w(TAG, "⚠️ Невалидное напряжение: $voltage V")
                    return null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка парсинга Voltage", e)
        }
        return null
    }

    // ===== ПАРСИНГ НАГРУЗКИ (0104) =====
    private fun parseEngineLoad(response: String): TelemetryData? {
        val dataBytes = extractDataBytes(response, "04")
        Log.d(TAG, "Engine Load Data: ${dataBytes.joinToString(" ")}")

        if (dataBytes.isEmpty()) {
            Log.w(TAG, "⚠️ Нет данных для нагрузки")
            return null
        }

        if (dataBytes.isNotEmpty()) {
            try {
                val load = (dataBytes[0].toInt() * 100) / 255
                if (load in 0..100) {
                    Log.d(TAG, "✅ Engine Load: $load%")
                    return TelemetryData(engineLoad = load.toFloat())
                } else {
                    Log.w(TAG, "⚠️ Невалидная нагрузка: $load%")
                    return null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка парсинга Engine Load", e)
                return null
            }
        }
        return null
    }

    // ===== ПАРСИНГ ТЕМПЕРАТУРЫ ВОЗДУХА (010F) =====
    private fun parseIntakeTemp(response: String): TelemetryData? {
        val dataBytes = extractDataBytes(response, "0F")
        Log.d(TAG, "Intake Temp Data: ${dataBytes.joinToString(" ")}")

        if (dataBytes.isEmpty()) {
            Log.w(TAG, "⚠️ Нет данных для температуры воздуха")
            return null
        }

        if (dataBytes.isNotEmpty()) {
            try {
                val raw = dataBytes[0].toInt()
                val temp = raw - 40

                if (temp > 10 && temp < 200) {
                    Log.d(TAG, "✅ Intake Temp: $temp°C")
                    return TelemetryData(intakeTemp = temp.toFloat())
                } else {
                    Log.w(TAG, "⚠️ Невалидная температура воздуха: $temp°C (raw=$raw)")
                    return null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка парсинга Intake Temp", e)
                return null
            }
        }
        return null
    }

    // ===== ПАРСИНГ ПОЛОЖЕНИЯ ДРОССЕЛЯ (0111) =====
    private fun parseThrottlePos(response: String): TelemetryData? {
        val dataBytes = extractDataBytes(response, "11")
        Log.d(TAG, "Throttle Position Data: ${dataBytes.joinToString(" ")}")

        if (dataBytes.isEmpty()) {
            Log.w(TAG, "⚠️ Нет данных для положения дросселя")
            return null
        }

        if (dataBytes.isNotEmpty()) {
            try {
                val pos = (dataBytes[0].toInt() * 100) / 255
                if (pos in 0..100) {
                    Log.d(TAG, "✅ Throttle Position: $pos%")
                    return TelemetryData(throttlePos = pos.toFloat())
                } else {
                    Log.w(TAG, "⚠️ Невалидное положение дросселя: $pos%")
                    return null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка парсинга Throttle Position", e)
                return null
            }
        }
        return null
    }

    // ===== ПАРСИНГ УРОВНЯ ТОПЛИВА (012F) =====
    private fun parseFuelLevel(response: String): TelemetryData? {
        val dataBytes = extractDataBytes(response, "2F")
        Log.d(TAG, "Fuel Level Data: ${dataBytes.joinToString(" ")}")

        if (dataBytes.isEmpty()) {
            Log.w(TAG, "⚠️ Нет данных для уровня топлива")
            return null
        }

        if (dataBytes.isNotEmpty()) {
            try {
                val level = (dataBytes[0].toInt() * 100) / 255
                if (level in 0..100) {
                    Log.d(TAG, "✅ Fuel Level: $level%")
                    return TelemetryData(fuelLevel = level.toFloat())
                } else {
                    Log.w(TAG, "⚠️ Невалидный уровень топлива: $level%")
                    return null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка парсинга Fuel Level", e)
                return null
            }
        }
        return null
    }

    // ===== ПАРСИНГ MAF (0110) =====
    private fun parseMAF(response: String): TelemetryData? {
        val dataBytes = extractDataBytes(response, "10")
        Log.d(TAG, "MAF Data: ${dataBytes.joinToString(" ")}")

        if (dataBytes.isEmpty()) {
            Log.w(TAG, "⚠️ Нет данных для MAF")
            return null
        }

        if (dataBytes.size >= 2) {
            try {
                val maf = ((dataBytes[0].toInt() shl 8) or dataBytes[1].toInt()) / 100.0f
                if (maf >= 0 && maf < 1000) {
                    Log.d(TAG, "✅ MAF: $maf г/с")
                    return TelemetryData(mafFlow = maf)
                } else {
                    Log.w(TAG, "⚠️ Невалидный MAF: $maf")
                    return null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка парсинга MAF", e)
                return null
            }
        }
        return null
    }

    // ===== ПАРСИНГ ДАВЛЕНИЯ ТОПЛИВА (010A) =====
    private fun parseFuelPressure(response: String): TelemetryData? {
        val dataBytes = extractDataBytes(response, "0A")
        Log.d(TAG, "Fuel Pressure Data: ${dataBytes.joinToString(" ")}")

        if (dataBytes.isEmpty()) {
            Log.w(TAG, "⚠️ Нет данных для давления топлива")
            return null
        }

        if (dataBytes.isNotEmpty()) {
            try {
                val pressure = dataBytes[0].toInt() * 3
                if (pressure in 0..1000) {
                    Log.d(TAG, "✅ Fuel Pressure: $pressure кПа")
                    return TelemetryData(fuelPressure = pressure.toFloat())
                } else {
                    Log.w(TAG, "⚠️ Невалидное давление топлива: $pressure")
                    return null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка парсинга Fuel Pressure", e)
                return null
            }
        }
        return null
    }

    // ===== ПАРСИНГ УГЛА ОПЕРЕЖЕНИЯ (010E) =====
    private fun parseTimingAdvance(response: String): TelemetryData? {
        val dataBytes = extractDataBytes(response, "0E")
        Log.d(TAG, "Timing Advance Data: ${dataBytes.joinToString(" ")}")

        if (dataBytes.isEmpty()) {
            Log.w(TAG, "⚠️ Нет данных для угла опережения")
            return null
        }

        if (dataBytes.isNotEmpty()) {
            try {
                val advance = (dataBytes[0].toInt() / 2.0) - 64.0
                if (advance in -64.0..64.0) {
                    Log.d(TAG, "✅ Timing Advance: $advance°")
                    return TelemetryData(timingAdvance = advance.toFloat())
                } else {
                    Log.w(TAG, "⚠️ Невалидный угол опережения: $advance")
                    return null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка парсинга Timing Advance", e)
                return null
            }
        }
        return null
    }

    // ===== ПАРСИНГ ПРОБЕГА (0146) =====
    private fun parseMileage(response: String): TelemetryData? {
        val dataBytes = extractDataBytes(response, "46")
        Log.d(TAG, "Mileage Data: ${dataBytes.joinToString(" ")}")

        if (dataBytes.isEmpty()) {
            Log.w(TAG, "⚠️ Нет данных для пробега")
            return null
        }

        if (dataBytes.size >= 4) {
            try {
                val mileage = (dataBytes[0].toInt() shl 24) or
                        (dataBytes[1].toInt() shl 16) or
                        (dataBytes[2].toInt() shl 8) or
                        dataBytes[3].toInt()
                if (mileage > 0 && mileage < 1000000) {
                    Log.d(TAG, "✅ Mileage: $mileage км")
                    return TelemetryData(mileage = mileage)
                } else {
                    Log.w(TAG, "⚠️ Невалидный пробег: $mileage")
                    return null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка парсинга Mileage", e)
                return null
            }
        }
        return null
    }

    // ===== ПАРСИНГ КОДОВ ОШИБОК (43) =====
    private fun parseDTC(response: String): TelemetryData? {
        val dtcCodes = mutableListOf<DTC>()
        val hexPattern = Regex("[0-9A-F]{4}")
        val matches = hexPattern.findAll(response)

        matches.forEach { match ->
            val code = match.value
            if (code != "0000") {
                dtcCodes.add(
                    DTC(
                        code = code,
                        description = getDTCDescription(code),
                        severity = getDTCSeverity(code)
                    )
                )
            }
        }

        return if (dtcCodes.isNotEmpty()) {
            TelemetryData(dtcCodes = dtcCodes)
        } else {
            null
        }
    }

    // ===== ВСПОМОГАТЕЛЬНЫЙ МЕТОД: ИЗВЛЕЧЕНИЕ БАЙТОВ =====
    private fun extractDataBytes(response: String, pid: String): List<Int> {
        var clean = response
            .replace(" ", "")
            .replace("\r", "")
            .replace("\n", "")
            .replace(">", "")
            .replace("BUS INIT:", "")
            .replace("UNABLE TO CONNECT", "")
            .replace("NO DATA", "")
            .replace("SEARCHING...", "")

        // ❌ Если ответ начинается с 7F - это ошибка
        if (clean.startsWith("7F")) {
            Log.w(TAG, "⚠️ Ответ об ошибке, пропускаем: $clean")
            return emptyList()
        }

        // ❌ Если ответ содержит 7F - тоже ошибка
        if (clean.contains("7F")) {
            Log.w(TAG, "⚠️ Ответ содержит ошибку, пропускаем: $clean")
            return emptyList()
        }

        Log.d(TAG, "clean response: $clean")

        // Ищем паттерн 41XX (где XX - PID)
        val pidPattern = "41$pid([0-9A-F]+)".toRegex(RegexOption.IGNORE_CASE)
        val match = pidPattern.find(clean)

        if (match != null) {
            val dataHex = match.groupValues[1]
            Log.d(TAG, "dataHex: $dataHex")

            // ❌ Если данных нет или они слишком короткие
            if (dataHex.isEmpty() || dataHex.length < 2) {
                Log.w(TAG, "⚠️ Нет данных для PID $pid")
                return emptyList()
            }

            val bytes = mutableListOf<Int>()
            for (i in 0 until dataHex.length step 2) {
                if (i + 1 < dataHex.length) {
                    try {
                        val byte = dataHex.substring(i, i + 2).toInt(16)
                        bytes.add(byte)
                    } catch (e: Exception) {
                        // Игнорируем ошибки
                    }
                }
            }
            return bytes
        }

        // Fallback: ищем любые hex-данные
        val hexMatch = Regex("[0-9A-F]{2,}").find(clean)
        if (hexMatch != null) {
            val hex = hexMatch.value
            // ❌ Если hex содержит ошибку - пропускаем
            if (hex.startsWith("7F")) {
                Log.w(TAG, "⚠️ Найдена ошибка в hex, пропускаем: $hex")
                return emptyList()
            }
            val bytes = mutableListOf<Int>()
            for (i in 0 until hex.length step 2) {
                if (i + 1 < hex.length) {
                    try {
                        val byte = hex.substring(i, i + 2).toInt(16)
                        bytes.add(byte)
                    } catch (e: Exception) {
                        // Игнорируем ошибки
                    }
                }
            }
            // Пропускаем заголовок (4 байта)
            if (bytes.size > 4) {
                return bytes.drop(4)
            }
            return bytes
        }

        return emptyList()
    }

    // ===== ОПИСАНИЯ КОДОВ ОШИБОК =====
    private fun getDTCDescription(code: String): String {
        return when (code) {
            "P0100" -> "Неисправность цепи массового расхода воздуха"
            "P0101" -> "Диапазон/рабочие характеристики цепи массового расхода воздуха"
            "P0102" -> "Низкий уровень сигнала в цепи массового расхода воздуха"
            "P0103" -> "Высокий уровень сигнала в цепи массового расхода воздуха"
            "P0110" -> "Неисправность цепи датчика температуры входящего воздуха"
            "P0115" -> "Неисправность цепи датчика температуры охлаждающей жидкости"
            "P0120" -> "Неисправность цепи датчика положения дроссельной заслонки"
            "P0121" -> "Диапазон/рабочие характеристики датчика положения дроссельной заслонки"
            "P0122" -> "Низкий уровень сигнала датчика положения дроссельной заслонки"
            "P0123" -> "Высокий уровень сигнала датчика положения дроссельной заслонки"
            "P0130" -> "Неисправность цепи кислородного датчика (банк 1, датчик 1)"
            "P0131" -> "Низкий уровень сигнала кислородного датчика (банк 1, датчик 1)"
            "P0132" -> "Высокий уровень сигнала кислородного датчика (банк 1, датчик 1)"
            "P0133" -> "Медленная реакция кислородного датчика (банк 1, датчик 1)"
            "P0134" -> "Отсутствие активности кислородного датчика (банк 1, датчик 1)"
            "P0135" -> "Неисправность цепи подогрева кислородного датчика (банк 1, датчик 1)"
            "P0170" -> "Неисправность топливной системы (банк 1)"
            "P0171" -> "Система слишком бедная (банк 1)"
            "P0172" -> "Система слишком богатая (банк 1)"
            "P0200" -> "Неисправность цепи управления форсунками"
            "P0300" -> "Пропуски зажигания в нескольких цилиндрах"
            "P0301" -> "Пропуски зажигания в цилиндре 1"
            "P0302" -> "Пропуски зажигания в цилиндре 2"
            "P0303" -> "Пропуски зажигания в цилиндре 3"
            "P0304" -> "Пропуски зажигания в цилиндре 4"
            "P0400" -> "Неисправность системы рециркуляции отработавших газов (EGR)"
            "P0420" -> "Эффективность катализатора ниже порога (банк 1)"
            "P0500" -> "Неисправность датчика скорости автомобиля"
            "P0505" -> "Неисправность системы управления холостым ходом"
            "P0600" -> "Ошибка связи с блоком управления"
            "P0601" -> "Ошибка контрольной суммы ПЗУ блока управления"
            "P0602" -> "Ошибка программирования блока управления"
            "P0700" -> "Неисправность системы управления трансмиссией"
            "P0730" -> "Неправильное передаточное число"
            "P0750" -> "Неисправность соленоида переключения передач A"
            "P0780" -> "Неисправность системы переключения передач"
            else -> "Код ошибки $code"
        }
    }

    private fun getDTCSeverity(code: String): String {
        return when (code.firstOrNull()) {
            'P' -> when (code.getOrNull(1)?.digitToIntOrNull()) {
                0 -> "Низкая"
                1 -> "Средняя"
                2 -> "Высокая"
                else -> "Средняя"
            }
            'C' -> "Средняя"
            'B' -> "Низкая"
            'U' -> "Средняя"
            else -> "Средняя"
        }
    }
}