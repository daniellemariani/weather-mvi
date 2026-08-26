package com.dmariani.weathermvi.data.repository

import com.dmariani.weathermvi.data.local.WeatherDao
import com.dmariani.weathermvi.data.remote.WeatherApi
import com.dmariani.weathermvi.data.toDomain
import com.dmariani.weathermvi.data.toEntity
import com.dmariani.weathermvi.domain.model.City
import com.dmariani.weathermvi.domain.model.Weather
import com.dmariani.weathermvi.domain.repository.WeatherRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherDao: WeatherDao,
    private val weatherApi: WeatherApi
) : WeatherRepository {

    companion object {
        private const val STALE_THRESHOLD_MS = 10 * 60 * 1000
    }

    override suspend fun getWeather(city: City, forceRefresh: Boolean): Result<Weather> {
        if (forceRefresh) {
            return fetchAndCacheWeather(city)
        }

        val now = System.currentTimeMillis()
        val cached = weatherDao.getOnce(city.name)

        return if (cached != null && (now - cached.cachedAt) < STALE_THRESHOLD_MS) {
            Result.success(cached.toDomain())
        } else {
            fetchAndCacheWeather(city)
        }
    }

    override fun observeRecentSearches(limit: Int): Flow<List<String>> {
        return weatherDao.recentCities(limit)
    }

    private suspend fun fetchAndCacheWeather(city: City): Result<Weather> {
        try {
            // fetch weather from API
            val response = weatherApi.getWeather(lat = city.lat, lon = city.lon)
            // store weather in DB
            val entity = response.toEntity(city.name, System.currentTimeMillis())
            weatherDao.upsert(entity)
            return Result.success(response.toDomain(city.name))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val cached = weatherDao.getOnce(city.name)
            return cached?.let {
                Result.success(it.toDomain())
            } ?: Result.failure(e)
        }
    }
}