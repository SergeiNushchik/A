package com.autojournal.data

import android.content.Context
import com.autojournal.data.model.CarBrand
import kotlinx.serialization.json.Json

object CarDatabaseLoader {
    
    private var cachedBrands: List<CarBrand>? = null
    
    fun loadBrands(context: Context): List<CarBrand> {
        cachedBrands?.let { return it }
        
        return try {
            val jsonString = context.assets.open("cars_db.json")
                .bufferedReader()
                .use { it.readText() }
            
            val brands = Json { 
                ignoreUnknownKeys = true 
                isLenient = true 
            }.decodeFromString<List<CarBrand>>(jsonString)
            
            cachedBrands = brands
            brands
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    /** Быстрый доступ к списку марок */
    fun getBrandNames(context: Context): List<String> {
        return loadBrands(context).map { it.name }
    }
    
    /** Модели для выбранной марки */
    fun getModelsForBrand(context: Context, brandName: String): List<String> {
        return loadBrands(context)
            .find { it.name == brandName }
            ?.models
            ?.map { it.name }
            ?: emptyList()
    }
    
    /** Поколения для выбранной модели */
    fun getGenerationsForModel(
        context: Context, 
        brandName: String, 
        modelName: String
    ): List<Pair<String, String>> { // name to years
        return loadBrands(context)
            .find { it.name == brandName }
            ?.models
            ?.find { it.name == modelName }
            ?.generations
            ?.map { it.name to it.years }
            ?: emptyList()
    }
}