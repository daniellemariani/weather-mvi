package com.dmariani.weathermvi.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Abstracts the app preferences (temperature-unit) behind DataStore.
 * [isFahrenheit] is observed independently by both [SettingsViewModel] and [WeatherViewModel], the latter
 * using it to convert displayed temperatures without touching the domain/data layers.
 */
interface SettingsRepository {

    val isFahrenheit: Flow<Boolean>

    suspend fun setIsFahrenheit(enabled: Boolean)
}