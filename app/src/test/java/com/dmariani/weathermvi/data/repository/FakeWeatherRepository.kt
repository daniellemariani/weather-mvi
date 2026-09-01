package com.dmariani.weathermvi.data.repository

import com.dmariani.weathermvi.domain.model.City
import com.dmariani.weathermvi.domain.model.Weather
import com.dmariani.weathermvi.domain.repository.WeatherRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.time.Duration.Companion.milliseconds

/**
 * Test double for [WeatherRepository]. Configure [forcedWeather] or [forcedError]
 * before calling [getWeather] to control the outcome; [delayMs] simulates a slow
 * network response. Throws if neither is set, so a forgotten setup fails loudly
 * rather than silently returning meaningless data.
 */
class FakeWeatherRepository : WeatherRepository {

    var forcedWeather: Weather? = null
    var forcedError: Throwable? = null
    var delayMs: Long = 0L

    val recentSearchesFlow = MutableStateFlow<List<String>>(emptyList())

    override suspend fun getWeather(city: City, forceRefresh: Boolean): Result<Weather> {
        if (delayMs > 0) delay(delayMs.milliseconds)

        return forcedWeather?.let { Result.success(it) }
            ?: forcedError?.let { Result.failure(it) }
            ?: error("No forced state configured")
    }

    override fun observeRecentSearches(limit: Int): Flow<List<String>> {
        return recentSearchesFlow
    }
}