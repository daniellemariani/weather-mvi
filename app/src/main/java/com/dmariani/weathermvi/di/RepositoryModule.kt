package com.dmariani.weathermvi.di

import com.dmariani.weathermvi.data.repository.SettingsRepositoryImpl
import com.dmariani.weathermvi.data.repository.WeatherRepositoryImpl
import com.dmariani.weathermvi.domain.repository.SettingsRepository
import com.dmariani.weathermvi.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    abstract fun provideSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}