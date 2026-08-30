package com.autojournal.di

import android.content.Context
import androidx.room.Room
import com.autojournal.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "autojournal.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideCarDao(database: AppDatabase) = database.carDao()

    @Provides
    fun provideExpenseDao(database: AppDatabase) = database.expenseDao()

    @Provides
    fun provideRepairTaskDao(database: AppDatabase) = database.repairTaskDao()
}