package com.autojournal.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.autojournal.data.local.dao.CarDao
import com.autojournal.data.local.dao.ExpenseDao
import com.autojournal.data.local.dao.RepairTaskDao
import com.autojournal.data.model.Car
import com.autojournal.data.model.Expense
import com.autojournal.data.model.RepairTask

@Database(
    entities = [Car::class, Expense::class, RepairTask::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun carDao(): CarDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun repairTaskDao(): RepairTaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // ===== МИГРАЦИЯ С 1 ДО 2 =====
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE cars ADD COLUMN fuelType TEXT NOT NULL DEFAULT 'Бензин'")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "autojournal.db"
                )
                    .addMigrations(MIGRATION_1_2)  // ← addMigrations (во множественном числе)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}