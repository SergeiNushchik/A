package com.autojournal.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SettingsPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_OBD_MODE = "obd_mode"
        private const val KEY_NETWORK_MODE = "network_mode"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_TENANT_CODE = "tenant_code"

        // ===== OBD НАСТРОЙКИ =====
        private const val KEY_OBD_ACTIVE_PIDS = "obd_active_pids"
        private const val KEY_OBD_UPDATE_INTERVAL = "obd_update_interval"
        private const val KEY_OBD_TIMEOUT = "obd_timeout"
        private const val KEY_OBD_CONNECTION_ATTEMPTS = "obd_connection_attempts"
        private const val KEY_OBD_AUTO_CONNECT = "obd_auto_connect"
        private const val KEY_OBD_USE_TEST_MODE = "obd_use_test_mode"
        private const val KEY_OBD_PROTOCOL = "obd_protocol"
        private const val KEY_OBD_PROTOCOL_NAME = "obd_protocol_name"

        // ===== ТЕМА И ВНЕШНИЙ ВИД =====
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_FUEL_PRICE = "fuel_price"
        private const val KEY_FUEL_UNIT = "fuel_unit"
        private const val KEY_LANGUAGE = "language"

        // ===== ДЛЯ ВИДЖЕТА =====
        private const val KEY_SELECTED_CAR_NAME = "selected_car_name"
        private const val KEY_LAST_MILEAGE = "last_mileage"
        private const val KEY_AVG_CONSUMPTION = "avg_consumption"
        private const val KEY_TOTAL_FUEL_COST = "total_fuel_cost"
        private const val KEY_FUEL_LEVEL = "fuel_level"
        private const val KEY_WIDGET_LAST_UPDATE = "widget_last_update"
    }

    // ===== БАЗОВЫЕ НАСТРОЙКИ =====
    fun isObdModeEnabled(): Boolean = prefs.getBoolean(KEY_OBD_MODE, true)
    fun setObdModeEnabled(enabled: Boolean) = prefs.edit { putBoolean(KEY_OBD_MODE, enabled) }

    fun isNetworkModeEnabled(): Boolean = prefs.getBoolean(KEY_NETWORK_MODE, true)
    fun setNetworkModeEnabled(enabled: Boolean) =
        prefs.edit { putBoolean(KEY_NETWORK_MODE, enabled) }

    fun getDeviceId(): String {
        var id = prefs.getString(KEY_DEVICE_ID, null)
        if (id == null) {
            id = java.util.UUID.randomUUID().toString()
            prefs.edit { putString(KEY_DEVICE_ID, id) }
        }
        return id!!
    }

    fun getTenantCode(): String? = prefs.getString(KEY_TENANT_CODE, null)
    fun setTenantCode(code: String?) = prefs.edit { putString(KEY_TENANT_CODE, code) }

    // ===== ТЕМА И ВНЕШНИЙ ВИД =====
    fun getThemeMode(): String = prefs.getString(KEY_THEME_MODE, "light") ?: "light"
    fun setThemeMode(mode: String) = prefs.edit { putString(KEY_THEME_MODE, mode) }

    fun getCurrency(): String = prefs.getString(KEY_CURRENCY, "₽") ?: "₽"
    fun setCurrency(currency: String) = prefs.edit { putString(KEY_CURRENCY, currency) }

    fun getFuelPrice(): Float? = prefs.getFloat(KEY_FUEL_PRICE, -1f).takeIf { it != -1f }
    fun setFuelPrice(price: Float) = prefs.edit { putFloat(KEY_FUEL_PRICE, price) }

    fun getFuelUnit(): String = prefs.getString(KEY_FUEL_UNIT, "liters") ?: "liters"
    fun setFuelUnit(unit: String) = prefs.edit { putString(KEY_FUEL_UNIT, unit) }

    fun getLanguage(): String = prefs.getString(KEY_LANGUAGE, "ru") ?: "ru"
    fun setLanguage(lang: String) = prefs.edit { putString(KEY_LANGUAGE, lang) }

    // ===== OBD НАСТРОЙКИ =====

    fun getActiveObdPids(): Set<String> {
        return prefs.getStringSet(KEY_OBD_ACTIVE_PIDS, defaultObdPids()) ?: defaultObdPids()
    }

    fun setActiveObdPids(pids: Set<String>) {
        prefs.edit { putStringSet(KEY_OBD_ACTIVE_PIDS, pids) }
    }

    fun isObdPidActive(pid: String): Boolean {
        return getActiveObdPids().contains(pid)
    }

    fun toggleObdPid(pid: String) {
        val current = getActiveObdPids().toMutableSet()
        if (current.contains(pid)) {
            current.remove(pid)
        } else {
            current.add(pid)
        }
        setActiveObdPids(current)
    }

    fun getObdUpdateInterval(): Long {
        return prefs.getLong(KEY_OBD_UPDATE_INTERVAL, 1000L)
    }

    fun setObdUpdateInterval(interval: Long) {
        prefs.edit { putLong(KEY_OBD_UPDATE_INTERVAL, interval) }
    }

    fun getObdTimeout(): Int {
        return prefs.getInt(KEY_OBD_TIMEOUT, 10)
    }

    fun setObdTimeout(timeout: Int) {
        prefs.edit { putInt(KEY_OBD_TIMEOUT, timeout) }
    }

    fun getObdConnectionAttempts(): Int {
        return prefs.getInt(KEY_OBD_CONNECTION_ATTEMPTS, 3)
    }

    fun setObdConnectionAttempts(attempts: Int) {
        prefs.edit { putInt(KEY_OBD_CONNECTION_ATTEMPTS, attempts) }
    }

    fun isObdAutoConnectEnabled(): Boolean {
        return prefs.getBoolean(KEY_OBD_AUTO_CONNECT, false)
    }

    fun setObdAutoConnectEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_OBD_AUTO_CONNECT, enabled) }
    }

    fun isObdTestModeEnabled(): Boolean {
        return prefs.getBoolean(KEY_OBD_USE_TEST_MODE, false)
    }

    fun setObdTestModeEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_OBD_USE_TEST_MODE, enabled) }
    }

    fun getObdProtocol(): String {
        return prefs.getString(KEY_OBD_PROTOCOL, "ATSP0") ?: "ATSP0"
    }

    fun setObdProtocol(protocol: String) {
        prefs.edit { putString(KEY_OBD_PROTOCOL, protocol) }
    }

    fun getObdProtocolName(): String {
        return prefs.getString(KEY_OBD_PROTOCOL_NAME, "Автоопределение") ?: "Автоопределение"
    }

    fun setObdProtocolName(name: String) {
        prefs.edit { putString(KEY_OBD_PROTOCOL_NAME, name) }
    }

    fun getProtocolsList(): List<ProtocolInfo> {
        return listOf(
            ProtocolInfo("ATSP0", "Автоопределение", "Автоматический выбор протокола", ""),
            ProtocolInfo("ATSP1", "SAE J1850 PWM", "Ford, Jaguar (до 2004)", "41.6 kbps"),
            ProtocolInfo("ATSP2", "SAE J1850 VPW", "GM, Chrysler (до 2004)", "10.4 kbps"),
            ProtocolInfo("ATSP3", "ISO 14230-4 KWP", "VW, Audi, BMW (до 2005)", "10.4 kbps"),
            ProtocolInfo("ATSP4", "ISO 14230-4 KWP Fast", "VW, Audi, BMW (до 2005)", "10.4 kbps"),
            ProtocolInfo("ATSP5", "ISO 9141-2", "Peugeot, Citroën, Fiat, Opel", "10.4 kbps"),
            ProtocolInfo("ATSP6", "ISO 15765-4 CAN", "Большинство авто после 2008", "500 kbps"),
            ProtocolInfo("ATSP7", "ISO 15765-4 CAN 29-bit", "Mercedes, BMW", "500 kbps"),
            ProtocolInfo("ATSP8", "SAE J1939 CAN", "Грузовики, тяжелая техника", "250 kbps"),
            ProtocolInfo("ATSP9", "ISO 15765-4 CAN (11-bit, 500k)", "CAN 11-bit 500k", "500 kbps"),
            ProtocolInfo("ATSP10", "ISO 15765-4 CAN (29-bit, 500k)", "CAN 29-bit 500k", "500 kbps"),
            ProtocolInfo("ATSP11", "ISO 15765-4 CAN (11-bit, 250k)", "CAN 11-bit 250k", "250 kbps"),
            ProtocolInfo("ATSP12", "ISO 15765-4 CAN (29-bit, 250k)", "CAN 29-bit 250k", "250 kbps")
        )
    }

    private fun defaultObdPids(): Set<String> {
        return setOf(
            "010C",  // RPM
            "010D",  // Speed
            "0105",  // Coolant Temp
            "ATRV",  // Voltage
            "0104",  // Engine Load
            "010F",  // Intake Temp
            "0111"   // Throttle Position
        )
    }

    fun getAllAvailablePids(): List<ObdPidInfo> {
        return listOf(
            ObdPidInfo("010C", "Обороты двигателя", "RPM", "🔄", recommended = true),
            ObdPidInfo("010D", "Скорость автомобиля", "Speed", "📊", recommended = true),
            ObdPidInfo(
                "0105",
                "Температура охлаждающей жидкости",
                "Coolant Temp",
                "🌡️",
                recommended = true
            ),
            ObdPidInfo("ATRV", "Напряжение бортовой сети", "Voltage", "⚡", recommended = true),
            ObdPidInfo("0104", "Нагрузка на двигатель", "Engine Load", "📈", recommended = true),
            ObdPidInfo(
                "010F",
                "Температура входящего воздуха",
                "Intake Temp",
                "🌬️",
                recommended = true
            ),
            ObdPidInfo(
                "0111",
                "Положение дроссельной заслонки",
                "Throttle",
                "🎯",
                recommended = true
            ),
            ObdPidInfo("012F", "Уровень топлива", "Fuel Level", "⛽", recommended = false),
            ObdPidInfo("0146", "Пробег (одометр)", "Mileage", "📏", recommended = false),
            ObdPidInfo("0110", "Массовый расход воздуха", "MAF", "💨", recommended = false),
            ObdPidInfo("010A", "Давление топлива", "Fuel Pressure", "🔧", recommended = false),
            ObdPidInfo("010E", "Угол опережения зажигания", "Timing", "⏱️", recommended = false)
        )
    }

    // ===== ДЛЯ ВИДЖЕТА =====

    fun getSelectedCarName(): String? = prefs.getString(KEY_SELECTED_CAR_NAME, null)
    fun setSelectedCarName(name: String) = prefs.edit { putString(KEY_SELECTED_CAR_NAME, name) }

    fun getLastMileage(): Int? = prefs.getInt(KEY_LAST_MILEAGE, -1).takeIf { it != -1 }
    fun setLastMileage(mileage: Int) = prefs.edit { putInt(KEY_LAST_MILEAGE, mileage) }

    fun getAverageConsumption(): Float? =
        prefs.getFloat(KEY_AVG_CONSUMPTION, -1f).takeIf { it != -1f }

    fun setAverageConsumption(consumption: Float) =
        prefs.edit { putFloat(KEY_AVG_CONSUMPTION, consumption) }

    fun getTotalFuelCost(): Float? = prefs.getFloat(KEY_TOTAL_FUEL_COST, -1f).takeIf { it != -1f }
    fun setTotalFuelCost(cost: Float) = prefs.edit { putFloat(KEY_TOTAL_FUEL_COST, cost) }

    fun getFuelLevel(): Float? = prefs.getFloat(KEY_FUEL_LEVEL, -1f).takeIf { it != -1f }
    fun setFuelLevel(level: Float) = prefs.edit { putFloat(KEY_FUEL_LEVEL, level) }

    fun getWidgetLastUpdate(): Long = prefs.getLong(KEY_WIDGET_LAST_UPDATE, 0L)
    fun setWidgetLastUpdate(time: Long) = prefs.edit { putLong(KEY_WIDGET_LAST_UPDATE, time) }

    fun clearWidgetData() {
        prefs.edit {
            remove(KEY_SELECTED_CAR_NAME)
            remove(KEY_LAST_MILEAGE)
            remove(KEY_AVG_CONSUMPTION)
            remove(KEY_TOTAL_FUEL_COST)
            remove(KEY_FUEL_LEVEL)
            remove(KEY_WIDGET_LAST_UPDATE)
        }
    }
    // ===== НОВЫЕ МЕТОДЫ ДЛЯ ВИДЖЕТА =====

    // Временные данные для виджета
    fun setTempFuelAmount(amount: Double) {
        prefs.edit().putFloat("temp_fuel_amount", amount.toFloat()).apply()
    }

    fun getTempFuelAmount(): Double {
        return prefs.getFloat("temp_fuel_amount", 0f).toDouble()
    }

    fun setTempFuelVolume(volume: Double) {
        prefs.edit().putFloat("temp_fuel_volume", volume.toFloat()).apply()
    }

    fun getTempFuelVolume(): Double {
        return prefs.getFloat("temp_fuel_volume", 0f).toDouble()
    }

    fun setSelectedFuelType(fuelType: String) {
        prefs.edit().putString("selected_fuel_type", fuelType).apply()
    }

    fun getSelectedFuelType(): String {
        return prefs.getString("selected_fuel_type", "АИ-95") ?: "АИ-95"
    }

    fun getSelectedCarFuelType(): String {
        return prefs.getString("selected_car_fuel_type", "Бензин") ?: "Бензин"
    }

    data class ProtocolInfo(
        val command: String,
        val name: String,
        val description: String,
        val speed: String
    )

    data class ObdPidInfo(
        val pid: String,
        val name: String,
        val shortName: String,
        val icon: String,
        val recommended: Boolean = false
    )
}