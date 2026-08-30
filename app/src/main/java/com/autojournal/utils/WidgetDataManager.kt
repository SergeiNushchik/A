package com.autojournal.utils

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.autojournal.data.model.Car
import com.autojournal.data.model.Expense
import com.autojournal.data.preferences.SettingsPreferences
import com.autojournal.ui.widget.RefuelWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object WidgetDataManager {

    fun updateWidgetData(context: Context, car: Car, expenses: List<Expense>) {
        val prefs = SettingsPreferences(context)

        prefs.setSelectedCarName("${car.brand} ${car.model}")
        prefs.setLastMileage(car.mileage)

        val fuelExpenses = expenses.filter {
            it.category.contains("Топливо") || it.category.contains("Заправка")
        }

        if (fuelExpenses.isNotEmpty()) {
            val totalFuelCost = fuelExpenses.sumOf { it.amount }
            prefs.setTotalFuelCost(totalFuelCost.toFloat())

            // Пример расчета расхода (заглушка)
            val avgConsumption = prefs.getAverageConsumption() ?: 8.5f
            prefs.setAverageConsumption(avgConsumption)
        }

        updateWidget(context)
    }

    fun updateWidget(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val manager = GlanceAppWidgetManager(context)
                val glanceIds = manager.getGlanceIds(RefuelWidget::class.java)
                glanceIds.forEach { id ->
                    RefuelWidget().update(context, id)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}