package com.autojournal.di

import com.autojournal.data.local.dao.CarDao
import com.autojournal.data.local.dao.ExpenseDao
import com.autojournal.data.local.dao.RepairTaskDao
import com.autojournal.data.repository.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAppRepository(
        carDao: CarDao,
        expenseDao: ExpenseDao,
        repairTaskDao: RepairTaskDao
    ): AppRepository {
        return AppRepository(carDao, expenseDao, repairTaskDao)
    }
}