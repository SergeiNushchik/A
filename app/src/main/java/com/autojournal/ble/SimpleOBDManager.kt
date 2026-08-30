package com.autojournal.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.autojournal.data.preferences.SettingsPreferences
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean

class SimpleOBDManager(
    private val context: Context
) {
    companion object {
        private const val TAG = "OBDManager"
        private const val SPP_UUID = "00001101-0000-1000-8000-00805f9b34fb"
        private const val READ_TIMEOUT = 10000L
        private const val COMMAND_DELAY = 80L
        private const val MAX_NO_DATA = 40
    }

    enum class ConnectionState {
        DISCONNECTED, CONNECTING, CONNECTED, READY, ERROR
    }

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _telemetry = MutableStateFlow(TelemetryData())
    val telemetry: StateFlow<TelemetryData> = _telemetry.asStateFlow()

    private val _protocolInfo = MutableStateFlow("Неизвестен")
    val protocolInfo: StateFlow<String> = _protocolInfo.asStateFlow()

    private var socket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    private val isRunning = AtomicBoolean(false)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val parser = Elm327Parser()
    private var noDataCounter = 0
    private var cycleCounter = 0

    private var updateIntervalMs = 1000L
    private var lastDevice: BluetoothDevice? = null

    private val prefs = SettingsPreferences(context)
    private var activePids = prefs.getActiveObdPids()

    fun setUpdateInterval(intervalMs: Long) {
        updateIntervalMs = intervalMs
        prefs.setObdUpdateInterval(intervalMs)
        Log.d(TAG, "⚙️ Интервал обновления установлен: ${intervalMs}ms")
    }

    fun refreshActivePids() {
        activePids = prefs.getActiveObdPids()
        Log.d(TAG, "🔄 Активные PID обновлены: $activePids")
    }

    // ===== ИНИЦИАЛИЗАЦИЯ ELM327 С ПЕРЕБОРОМ ПРОТОКОЛОВ =====
    private suspend fun initializeELM327(): Boolean {
        try {
            Log.d(TAG, "🔄 Инициализация ELM327...")

            // ===== ЖЁСТКИЙ СБРОС =====
            var reset = sendCommand("ATZ\r\n", timeout = 3000)
            Log.d(TAG, "ATZ ответ: $reset")
            if (reset == null || !reset.contains("ELM327", ignoreCase = true)) {
                Log.e(TAG, "❌ Ошибка сброса ELM327")
                return false
            }
            delay(500)

            sendCommand("ATE0\r\n")
            delay(300)

            // ===== ПРОВЕРКА STI/VTI =====
            val sti = sendCommand("STI\r\n")
            if (sti != null && sti.contains("?")) {
                Log.d(TAG, "STI не поддерживается")
            }
            delay(200)

            val vti = sendCommand("VTI\r\n")
            if (vti != null && vti.contains("?")) {
                Log.d(TAG, "VTI не поддерживается")
            }
            delay(200)

            // ===== ПОВТОРНЫЙ СБРОС =====
            sendCommand("ATZ\r\n")
            delay(500)
            sendCommand("ATE0\r\n")
            delay(300)

            // ===== ПОЛУЧАЕМ ВЫБРАННЫЙ ПРОТОКОЛ ИЗ НАСТРОЕК =====
            val selectedProtocol = prefs.getObdProtocol()
            val selectedProtocolName = prefs.getObdProtocolName()

            Log.d(TAG, "📡 Выбранный протокол: $selectedProtocolName ($selectedProtocol)")

            // ===== ПЕРЕБОР ПРОТОКОЛОВ =====
            val protocols = if (selectedProtocol == "ATSP0") {
                // Если автоопределение - перебираем все
                prefs.getProtocolsList().filter { it.command != "ATSP0" }
            } else {
                // Если выбран конкретный - пробуем только его
                prefs.getProtocolsList().filter { it.command == selectedProtocol }
            }

            var protocolFound = false
            var foundProtocolName = "Неизвестен"

            for (protocol in protocols) {
                Log.d(TAG, "🔍 Пробуем протокол: ${protocol.name} (${protocol.command})")

                // Сброс перед сменой протокола
                sendCommand("ATZ\r\n")
                delay(300)
                sendCommand("ATE0\r\n")
                delay(200)

                val result = sendCommand("${protocol.command}\r\n")
                if (result == null || !result.contains("OK", ignoreCase = true)) {
                    Log.w(TAG, "⚠️ Не удалось установить ${protocol.command}")
                    continue
                }
                delay(300)

                // Устанавливаем заголовок в зависимости от протокола
                when {
                    protocol.command == "ATSP3" || protocol.command == "ATSP4" || protocol.command == "ATSP5" -> {
                        // ISO 9141-2 / KWP
                        sendCommand("ATSH8110F1\r\n")
                        delay(200)
                        sendCommand("ATH1\r\n")
                        delay(200)
                    }
                    protocol.command == "ATSP6" || protocol.command == "ATSP7" ||
                            protocol.command.startsWith("ATSP") && protocol.command.length > 4 -> {
                        // CAN
                        sendCommand("ATSH7E0\r\n")
                        delay(200)
                        sendCommand("ATH1\r\n")
                        delay(200)
                    }
                    else -> {
                        // J1850
                        sendCommand("ATH1\r\n")
                        delay(200)
                    }
                }

                sendCommand("ATS0\r\n")
                delay(200)
                sendCommand("ATM0\r\n")
                delay(200)
                sendCommand("ATAT1\r\n")
                delay(200)

                // Проверка связи с ECU
                val test0100 = sendCommand("0100\r\n", timeout = 5000)
                if (test0100 != null &&
                    !test0100.contains("UNABLE TO CONNECT", ignoreCase = true) &&
                    !test0100.contains("NO DATA", ignoreCase = true) &&
                    test0100.contains("41", ignoreCase = true)) {

                    Log.d(TAG, "✅ Протокол найден: ${protocol.name}")
                    protocolFound = true
                    foundProtocolName = protocol.name
                    _protocolInfo.value = protocol.name

                    // Сохраняем найденный протокол
                    if (selectedProtocol == "ATSP0") {
                        prefs.setObdProtocol(protocol.command)
                        prefs.setObdProtocolName(protocol.name)
                    }
                    break
                } else {
                    Log.w(TAG, "⚠️ Протокол ${protocol.name} не сработал")
                }
            }

            if (!protocolFound) {
                Log.e(TAG, "❌ Не удалось найти подходящий протокол")
                _protocolInfo.value = "Протокол не найден"
                return false
            }

            // ===== ДОПОЛНИТЕЛЬНЫЕ ЗАПРОСЫ =====
            sendCommand("0120\r\n", timeout = 3000)
            delay(300)

            sendCommand("0902\r\n")
            delay(300)
            sendCommand("0904\r\n")
            delay(300)
            sendCommand("090A\r\n")
            delay(300)

            Log.d(TAG, "✅ ELM327 инициализирован успешно! Протокол: $foundProtocolName")
            return true

        } catch (e: Exception) {
            Log.e(TAG, "❌ Ошибка инициализации: ${e.message}", e)
            return false
        }
    }

    private suspend fun sendCommand(command: String, timeout: Long = READ_TIMEOUT): String? {
        return withTimeoutOrNull(timeout) {
            try {
                outputStream?.write(command.toByteArray(Charsets.ISO_8859_1))
                outputStream?.flush()
                Log.v(TAG, "📤 Отправлено: ${command.trim()}")

                val response = readResponse()
                Log.v(TAG, "📥 Ответ: $response")

                if (response != null && response.contains("NO DATA", ignoreCase = true)) {
                    noDataCounter++
                    Log.w(TAG, "⚠️ NO DATA (${noDataCounter}/$MAX_NO_DATA)")
                    if (noDataCounter >= MAX_NO_DATA) {
                        Log.e(TAG, "❌ Превышен порог NO DATA, переподключение...")
                        reconnectAndReinit()
                        noDataCounter = 0
                    }
                    return@withTimeoutOrNull null
                }

                noDataCounter = 0

                if (response != null &&
                    (response.contains("ERROR", ignoreCase = true) ||
                            response.contains("UNABLE TO CONNECT", ignoreCase = true))) {
                    Log.w(TAG, "⚠️ Получена ошибка: $response")
                    return@withTimeoutOrNull null
                }

                response
            } catch (e: Exception) {
                Log.e(TAG, "❌ Ошибка отправки команды: ${e.message}")
                null
            }
        }
    }

    private suspend fun readResponse(): String? {
        val buffer = ByteArray(1024)
        var response = ""

        try {
            while (true) {
                if (inputStream?.available() ?: 0 > 0) {
                    val bytes = inputStream?.read(buffer) ?: 0
                    if (bytes > 0) {
                        val chunk = String(buffer, 0, bytes, Charsets.ISO_8859_1)
                        response += chunk
                        if (response.contains(">")) {
                            break
                        }
                    }
                }
                delay(20)
            }

            val cleanResponse = response
                .replace("\r", "")
                .replace("\n", "")
                .replace(">", "")
                .trim()

            return if (cleanResponse.isNotEmpty()) cleanResponse else null

        } catch (e: Exception) {
            Log.e(TAG, "❌ Ошибка чтения: ${e.message}")
            return null
        }
    }

    private suspend fun reconnectAndReinit() {
        Log.d(TAG, "🔄 Переподключение...")
        disconnect()
        delay(1000)
        lastDevice?.let { connect(it) }
    }

    // ===== ЦИКЛ ЧТЕНИЯ ДАННЫХ =====
    private fun startReadingLoop() {
        isRunning.set(true)
        cycleCounter = 0
        scope.launch {
            while (isRunning.get() && _connectionState.value == ConnectionState.READY) {
                try {
                    refreshActivePids()

                    // RPM (010C)
                    if (activePids.contains("010C")) {
                        sendCommand("010C\r\n")?.let {
                            parser.parseResponse(it)?.let { updateTelemetry(it) }
                        }
                        delay(COMMAND_DELAY)
                    }

                    // Speed (010D)
                    if (activePids.contains("010D")) {
                        sendCommand("010D\r\n")?.let {
                            parser.parseResponse(it)?.let { updateTelemetry(it) }
                        }
                        delay(COMMAND_DELAY)
                    }

                    // Coolant Temp (0105)
                    if (activePids.contains("0105")) {
                        sendCommand("0105\r\n")?.let {
                            parser.parseResponse(it)?.let { updateTelemetry(it) }
                        }
                        delay(COMMAND_DELAY)
                    }

                    // Voltage (ATRV)
                    if (activePids.contains("ATRV")) {
                        sendCommand("ATRV\r\n")?.let {
                            parser.parseResponse(it)?.let { updateTelemetry(it) }
                        }
                        delay(COMMAND_DELAY)
                    }

                    // Engine Load (0104)
                    if (activePids.contains("0104")) {
                        sendCommand("0104\r\n")?.let {
                            parser.parseResponse(it)?.let { updateTelemetry(it) }
                        }
                        delay(COMMAND_DELAY)
                    }

                    // Intake Temp (010F)
                    if (activePids.contains("010F")) {
                        sendCommand("010F\r\n")?.let {
                            parser.parseResponse(it)?.let { updateTelemetry(it) }
                        }
                        delay(COMMAND_DELAY)
                    }

                    // Throttle Position (0111)
                    if (activePids.contains("0111")) {
                        sendCommand("0111\r\n")?.let {
                            parser.parseResponse(it)?.let { updateTelemetry(it) }
                        }
                        delay(COMMAND_DELAY)
                    }

                    // Fuel Level (012F) - может не работать на старых авто
                    if (activePids.contains("012F")) {
                        sendCommand("012F\r\n")?.let {
                            parser.parseResponse(it)?.let { updateTelemetry(it) }
                        }
                        delay(COMMAND_DELAY)
                    }

                    // Mileage (0146) - может не работать на старых авто
                    if (activePids.contains("0146")) {
                        sendCommand("0146\r\n")?.let {
                            parser.parseResponse(it)?.let { updateTelemetry(it) }
                        }
                        delay(COMMAND_DELAY)
                    }

                    // MAF (0110) - требует CAN
                    if (activePids.contains("0110")) {
                        sendCommand("0110\r\n")?.let {
                            parser.parseResponse(it)?.let { updateTelemetry(it) }
                        }
                        delay(COMMAND_DELAY)
                    }

                    // Fuel Pressure (010A) - требует CAN
                    if (activePids.contains("010A")) {
                        sendCommand("010A\r\n")?.let {
                            parser.parseResponse(it)?.let { updateTelemetry(it) }
                        }
                        delay(COMMAND_DELAY)
                    }

                    // Timing Advance (010E) - требует CAN
                    if (activePids.contains("010E")) {
                        sendCommand("010E\r\n")?.let {
                            parser.parseResponse(it)?.let { updateTelemetry(it) }
                        }
                        delay(COMMAND_DELAY)
                    }

                    // DTC (03) - раз в 5 циклов
                    cycleCounter++
                    if (cycleCounter % 5 == 0) {
                        sendCommand("03\r\n")?.let {
                            parser.parseResponse(it)?.let { updateTelemetry(it) }
                        }
                    }

                    delay(updateIntervalMs)

                } catch (e: Exception) {
                    Log.e(TAG, "❌ Ошибка в цикле: ${e.message}")
                    if (e is IOException) {
                        _connectionState.value = ConnectionState.ERROR
                        isRunning.set(false)
                        break
                    }
                    delay(1000)
                }
            }
            Log.d(TAG, "⏹️ Цикл чтения завершен")
        }
    }

    // ===== ОБНОВЛЕНИЕ ТЕЛЕМЕТРИИ =====
    private fun updateTelemetry(newData: TelemetryData) {
        val current = _telemetry.value

        _telemetry.value = TelemetryData(
            rpm = if (newData.rpm > 0) newData.rpm else current.rpm,
            speed = if (newData.speed >= 0) newData.speed else current.speed,
            coolantTemp = if (newData.coolantTemp > 10 && newData.coolantTemp < 300) newData.coolantTemp else current.coolantTemp,
            voltage = if (newData.voltage > 6 && newData.voltage < 18) newData.voltage else current.voltage,
            mileage = if (newData.mileage > 0) newData.mileage else current.mileage,
            fuelLevel = if (newData.fuelLevel >= 0 && newData.fuelLevel <= 100) newData.fuelLevel else current.fuelLevel,
            engineLoad = if (newData.engineLoad >= 0 && newData.engineLoad <= 100) newData.engineLoad else current.engineLoad,
            intakeTemp = if (newData.intakeTemp > 10 && newData.intakeTemp < 200) newData.intakeTemp else current.intakeTemp,
            throttlePos = if (newData.throttlePos >= 0 && newData.throttlePos <= 100) newData.throttlePos else current.throttlePos,
            mafFlow = if (newData.mafFlow >= 0) newData.mafFlow else current.mafFlow,
            fuelPressure = if (newData.fuelPressure > 0) newData.fuelPressure else current.fuelPressure,
            timingAdvance = if (newData.timingAdvance > -64 && newData.timingAdvance < 64) newData.timingAdvance else current.timingAdvance,
            oxygenSensor1 = if (newData.oxygenSensor1 > 0 && newData.oxygenSensor1 < 5) newData.oxygenSensor1 else current.oxygenSensor1,
            oxygenSensor2 = if (newData.oxygenSensor2 > 0 && newData.oxygenSensor2 < 5) newData.oxygenSensor2 else current.oxygenSensor2,
            engineRuntime = if (newData.engineRuntime > 0) newData.engineRuntime else current.engineRuntime,
            distanceWithMIL = if (newData.distanceWithMIL >= 0) newData.distanceWithMIL else current.distanceWithMIL,
            dtcCodes = if (newData.dtcCodes.isNotEmpty()) newData.dtcCodes else current.dtcCodes,
            timestamp = System.currentTimeMillis(),
            isConnected = current.isConnected
        )
    }

    // ===== ПОДКЛЮЧЕНИЕ С ТАЙМАУТОМ =====
    fun connect(device: BluetoothDevice) {
        Log.d(TAG, "🔗 Подключение к ${device.name} (${device.address})")
        lastDevice = device
        _connectionState.value = ConnectionState.CONNECTING

        scope.launch {
            try {
                try {
                    socket?.close()
                } catch (e: Exception) { }
                socket = null

                val connectJob = async {
                    socket = device.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID))
                    socket?.connect()
                }

                withTimeout(5000L) {
                    connectJob.await()
                }

                inputStream = socket?.inputStream
                outputStream = socket?.outputStream

                if (inputStream != null && outputStream != null) {
                    _connectionState.value = ConnectionState.CONNECTED
                    Log.d(TAG, "✅ Сокет подключен")

                    val initialized = initializeELM327()
                    if (initialized) {
                        _connectionState.value = ConnectionState.READY
                        Log.d(TAG, "✅ ELM327 инициализирован")
                        refreshActivePids()
                        startReadingLoop()
                    } else {
                        _connectionState.value = ConnectionState.ERROR
                        Log.e(TAG, "❌ Ошибка инициализации ELM327")
                        delay(1000)
                        recoverConnection()
                    }
                } else {
                    _connectionState.value = ConnectionState.ERROR
                    Log.e(TAG, "❌ Не удалось получить потоки")
                }
            } catch (e: TimeoutException) {
                Log.e(TAG, "❌ Таймаут подключения")
                _connectionState.value = ConnectionState.ERROR
                Toast.makeText(context, "⏰ Таймаут подключения", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Log.e(TAG, "❌ Ошибка подключения: ${e.message}")
                _connectionState.value = ConnectionState.ERROR
                closeConnection()
            }
        }
    }

    fun disconnect() {
        Log.d(TAG, "🔌 Отключение")
        isRunning.set(false)
        closeConnection()
        _connectionState.value = ConnectionState.DISCONNECTED
        _telemetry.value = TelemetryData()
        _protocolInfo.value = "Неизвестен"
    }

    private fun closeConnection() {
        try {
            inputStream?.close()
            outputStream?.close()
            socket?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка закрытия", e)
        }
        inputStream = null
        outputStream = null
        socket = null
    }

    // ===== ПУБЛИЧНЫЕ МЕТОДЫ =====
    fun requestAllData() {
        Log.d(TAG, "📤 Запрос всех данных...")
    }

    fun requestRPM() {
        scope.launch {
            sendCommand("010C\r\n")?.let { parser.parseResponse(it) }?.let { updateTelemetry(it) }
        }
    }

    fun requestSpeed() {
        scope.launch {
            sendCommand("010D\r\n")?.let { parser.parseResponse(it) }?.let { updateTelemetry(it) }
        }
    }

    fun requestVoltage() {
        scope.launch {
            sendCommand("ATRV\r\n")?.let { parser.parseResponse(it) }?.let { updateTelemetry(it) }
        }
    }

    fun requestCoolantTemp() {
        scope.launch {
            sendCommand("0105\r\n")?.let { parser.parseResponse(it) }?.let { updateTelemetry(it) }
        }
    }

    fun requestEngineLoad() {
        scope.launch {
            sendCommand("0104\r\n")?.let { parser.parseResponse(it) }?.let { updateTelemetry(it) }
        }
    }

    fun requestIntakeTemp() {
        scope.launch {
            sendCommand("010F\r\n")?.let { parser.parseResponse(it) }?.let { updateTelemetry(it) }
        }
    }

    fun requestThrottlePos() {
        scope.launch {
            sendCommand("0111\r\n")?.let { parser.parseResponse(it) }?.let { updateTelemetry(it) }
        }
    }

    fun requestMileage() {
        scope.launch {
            Log.d(TAG, "📤 Запрос пробега (0146)...")
            val response = sendCommand("0146\r\n")
            Log.d(TAG, "0146 ответ: $response")
            response?.let { parser.parseResponse(it) }?.let { updateTelemetry(it) }
        }
    }

    fun requestMileageAlt1() {
        scope.launch {
            Log.d(TAG, "📤 Запрос пробега (01A6)...")
            val response = sendCommand("01A6\r\n")
            Log.d(TAG, "01A6 ответ: $response")
            response?.let { parser.parseResponse(it) }?.let { updateTelemetry(it) }
        }
    }

    fun requestMileageAlt2() {
        scope.launch {
            Log.d(TAG, "📤 Запрос пробега (0131)...")
            val response = sendCommand("0131\r\n")
            Log.d(TAG, "0131 ответ: $response")
            response?.let { parser.parseResponse(it) }?.let { updateTelemetry(it) }
        }
    }

    fun requestMileageAlt3() {
        scope.launch {
            Log.d(TAG, "📤 Запрос пробега (21A6)...")
            val response = sendCommand("21A6\r\n")
            Log.d(TAG, "21A6 ответ: $response")
            response?.let { parser.parseResponse(it) }?.let { updateTelemetry(it) }
        }
    }

    fun requestFuelLevel() {
        scope.launch {
            Log.d(TAG, "📤 Запрос уровня топлива (012F)...")
            val response = sendCommand("012F\r\n")
            Log.d(TAG, "012F ответ: $response")
            response?.let { parser.parseResponse(it) }?.let { updateTelemetry(it) }
        }
    }

    fun requestMAF() {
        scope.launch {
            Log.d(TAG, "📤 Запрос MAF (0110)...")
            val response = sendCommand("0110\r\n")
            Log.d(TAG, "0110 ответ: $response")
            response?.let { parser.parseResponse(it) }?.let { updateTelemetry(it) }
        }
    }

    fun requestFuelPressure() {
        scope.launch {
            Log.d(TAG, "📤 Запрос давления топлива (010A)...")
            val response = sendCommand("010A\r\n")
            Log.d(TAG, "010A ответ: $response")
            response?.let { parser.parseResponse(it) }?.let { updateTelemetry(it) }
        }
    }

    fun requestTimingAdvance() {
        scope.launch {
            Log.d(TAG, "📤 Запрос угла опережения (010E)...")
            val response = sendCommand("010E\r\n")
            Log.d(TAG, "010E ответ: $response")
            response?.let { parser.parseResponse(it) }?.let { updateTelemetry(it) }
        }
    }

    fun requestDTC() {
        scope.launch {
            sendCommand("03\r\n")?.let { parser.parseResponse(it) }?.let { updateTelemetry(it) }
        }
    }

    fun clearDTC() {
        scope.launch {
            sendCommand("04\r\n")
            Log.d(TAG, "🧹 Коды ошибок сброшены")
        }
    }

    fun setKWPProtocol() {
        scope.launch {
            sendCommand("ATZ\r\n")
            delay(300)
            sendCommand("ATE0\r\n")
            delay(200)
            sendCommand("ATSP3\r\n")
            delay(200)
            _protocolInfo.value = "KWP2000 (ISO 14230-4)"
            prefs.setObdProtocol("ATSP3")
            prefs.setObdProtocolName("KWP2000 (ISO 14230-4)")
            Log.d(TAG, "🔧 Установлен протокол KWP")
        }
    }

    fun setCANProtocol() {
        scope.launch {
            sendCommand("ATZ\r\n")
            delay(300)
            sendCommand("ATE0\r\n")
            delay(200)
            sendCommand("ATSP6\r\n")
            delay(200)
            _protocolInfo.value = "ISO 15765-4 CAN"
            prefs.setObdProtocol("ATSP6")
            prefs.setObdProtocolName("ISO 15765-4 CAN")
            Log.d(TAG, "🔧 Установлен протокол CAN")
        }
    }

    fun setISO9141Protocol() {
        scope.launch {
            sendCommand("ATZ\r\n")
            delay(300)
            sendCommand("ATE0\r\n")
            delay(200)
            sendCommand("ATSP5\r\n")
            delay(200)
            _protocolInfo.value = "ISO 9141-2"
            prefs.setObdProtocol("ATSP5")
            prefs.setObdProtocolName("ISO 9141-2")
            Log.d(TAG, "🔧 Установлен протокол ISO 9141-2")
        }
    }

    fun fastInit() {
        scope.launch {
            sendCommand("ATFI\r\n")
            Log.d(TAG, "⚡ Быстрая инициализация")
        }
    }

    fun requestProtocol() {
        scope.launch {
            val response = sendCommand("ATDP\r\n")
            _protocolInfo.value = response?.trim() ?: "Неизвестен"
            Log.d(TAG, "📡 Протокол: ${_protocolInfo.value}")
        }
    }

    fun checkAdapterMode() {
        scope.launch {
            val response = sendCommand("ATRV\r\n")
            Log.d(TAG, "🔌 Напряжение адаптера: $response")
        }
    }

    fun testKWP() {
        scope.launch {
            Log.d(TAG, "🧪 Тестирование KWP...")
            sendCommand("ATZ\r\n")
            delay(300)
            sendCommand("ATE0\r\n")
            delay(200)
            sendCommand("ATSP3\r\n")
            delay(300)
            sendCommand("ATFI\r\n")
            delay(300)
            sendCommand("ATDP\r\n")
            delay(300)
            sendCommand("010C\r\n")
            Log.d(TAG, "🧪 Тест KWP выполнен")
        }
    }

    fun resetAdapter() {
        scope.launch {
            Log.d(TAG, "🔄 Сброс адаптера...")
            sendCommand("ATZ\r\n")
            delay(500)
            sendCommand("ATE0\r\n")
            delay(300)
            sendCommand("ATSP0\r\n")
            delay(300)
            Log.d(TAG, "✅ Адаптер сброшен")

            val response = sendCommand("ATDP\r\n")
            _protocolInfo.value = response?.trim() ?: "Неизвестен"
        }
    }

    fun recoverConnection() {
        scope.launch {
            Log.d(TAG, "🔄 Восстановление соединения...")
            disconnect()
            delay(2000)

            try {
                socket?.close()
            } catch (e: Exception) { }
            socket = null
            inputStream = null
            outputStream = null

            _connectionState.value = ConnectionState.DISCONNECTED
            _telemetry.value = TelemetryData()
            _protocolInfo.value = "Неизвестен"

            lastDevice?.let {
                Log.d(TAG, "🔄 Повторное подключение к ${it.name}")
                connect(it)
            }
        }
    }

    fun hardReset() {
        scope.launch {
            Log.d(TAG, "🔴 ПРИНУДИТЕЛЬНЫЙ СБРОС АДАПТЕРА...")
            disconnect()
            delay(1000)
            lastDevice?.let {
                Log.d(TAG, "🔄 Попытка переподключения к ${it.name}")
                connect(it)
            }
        }
    }

    fun cleanup() {
        isRunning.set(false)
        disconnect()
        scope.cancel()
    }

    // ===== ТЕСТОВЫЙ РЕЖИМ =====
    private var isTestMode = false
    private var testCounter = 0

    fun enableTestMode() {
        Log.d(TAG, "🧪 Тестовый режим ВКЛЮЧЕН")
        isTestMode = true
        _connectionState.value = ConnectionState.CONNECTED
        _protocolInfo.value = "ТЕСТОВЫЙ РЕЖИМ"
        prefs.setObdTestModeEnabled(true)
        startTestDataGeneration()
    }

    fun disableTestMode() {
        Log.d(TAG, "🧪 Тестовый режим ВЫКЛЮЧЕН")
        isTestMode = false
        _connectionState.value = ConnectionState.DISCONNECTED
        _telemetry.value = TelemetryData()
        _protocolInfo.value = "Неизвестен"
        prefs.setObdTestModeEnabled(false)
    }

    private fun startTestDataGeneration() {
        scope.launch {
            while (isTestMode) {
                testCounter++
                _telemetry.value = TelemetryData(
                    rpm = 800f + (testCounter % 20) * 50f,
                    speed = (testCounter % 6) * 20f,
                    coolantTemp = 85f + (testCounter % 5),
                    voltage = 12.0f + (testCounter % 5) * 0.1f,
                    mileage = 50000 + testCounter,
                    fuelLevel = 60f + (testCounter % 5) * 2f,
                    engineLoad = 20f + (testCounter % 10) * 3f,
                    intakeTemp = 25f + (testCounter % 8),
                    throttlePos = 10f + (testCounter % 10) * 2f,
                    mafFlow = 5f + (testCounter % 10) * 0.5f,
                    fuelPressure = 300f + (testCounter % 10) * 5f,
                    timingAdvance = 10f + (testCounter % 6),
                    oxygenSensor1 = 0.1f + (testCounter % 10) * 0.05f,
                    oxygenSensor2 = 0.1f + (testCounter % 8) * 0.04f,
                    engineRuntime = testCounter * 10,
                    distanceWithMIL = if (testCounter % 20 == 0) 150 else 0,
                    dtcCodes = if (testCounter % 30 == 0) {
                        listOf(DTC("P0300", "Пропуски зажигания", "Средняя"))
                    } else emptyList()
                )
                delay(1000)
            }
        }
    }
}