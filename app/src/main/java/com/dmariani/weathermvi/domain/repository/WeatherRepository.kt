package com.dmariani.weathermvi.domain.repository

import com.dmariani.weathermvi.domain.model.City
import com.dmariani.weathermvi.domain.model.Weather
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for weather data, abstracting over both remote (network/API)
 * and local (Room).
 */
interface WeatherRepository {

    suspend fun getWeather(city: City, forceRefresh: Boolean = false): Result<Weather>
    fun observeRecentSearches(limit: Int = 5): Flow<List<String>>
}