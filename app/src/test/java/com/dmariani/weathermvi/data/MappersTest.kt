package com.dmariani.weathermvi.data

import com.dmariani.weathermvi.data.local.WeatherEntity
import com.dmariani.weathermvi.data.remote.CurrentWeatherResponse
import com.dmariani.weathermvi.data.remote.WeatherResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class MappersTest {

    @Test
    fun `validate Clear Day weather from response`() {
        val current = CurrentWeatherResponse(
            temperature = 28.0,
            weatherCode = 0,
            isDay = 1
        )

        val weather = WeatherResponse(current).toDomain("Los Angeles")
        assertEquals("Los Angeles", weather.city)
        assertEquals(28.0, weather.temperature, 0.0)
        assertEquals("Clear", weather.condition)
        assertEquals(true, weather.isDay)
    }

    @Test
    fun `validate Clear Day weather from entity`() {
        val entity = WeatherEntity(
            city = "Los Angeles",
            temperature = 28.0,
            weatherCode = 0,
            isDay = true,
            cachedAt = System.currentTimeMillis()
        )

        val weather = entity.toDomain()
        assertEquals("Los Angeles", weather.city)
        assertEquals(28.0, weather.temperature, 0.0)
        assertEquals("Clear", weather.condition)
        assertEquals(true, weather.isDay)
    }

    @Test
    fun `validate Clear Day entity from response`() {
        val current = CurrentWeatherResponse(
            temperature = 28.0,
            weatherCode = 0,
            isDay = 1
        )

        val timestamp = System.currentTimeMillis()
        val entity = WeatherResponse(current).toEntity("Los Angeles", timestamp)
        assertEquals("Los Angeles", entity.city)
        assertEquals(current.temperature, entity.temperature, 0.0)
        assertEquals(current.weatherCode, entity.weatherCode)
        assertEquals(true, entity.isDay)
        assertEquals(timestamp, entity.cachedAt)
    }

    @Test
    fun `validate Rainy Night weather from response`() {
        val current = CurrentWeatherResponse(
            temperature = 14.0,
            weatherCode = 51,
            isDay = 0
        )

        val weather = WeatherResponse(current).toDomain("London")
        assertEquals("London", weather.city)
        assertEquals(14.0, weather.temperature, 0.0)
        assertEquals("Rain", weather.condition)
        assertEquals(false, weather.isDay)
    }

    @Test
    fun `validate Rainy Night weather from entity`() {
        val entity = WeatherEntity(
            city = "London",
            temperature = 14.0,
            weatherCode = 51,
            isDay = false,
            cachedAt = System.currentTimeMillis()
        )

        val weather = entity.toDomain()
        assertEquals("London", weather.city)
        assertEquals(14.0, weather.temperature, 0.0)
        assertEquals("Rain", weather.condition)
        assertEquals(false, weather.isDay)
    }

    @Test
    fun `validate Unknown weather from response`() {
        val current = CurrentWeatherResponse(
            temperature = 24.0,
            weatherCode = 4,
            isDay = 1
        )

        val weather = WeatherResponse(current).toDomain("Paris")
        assertEquals("Paris", weather.city)
        assertEquals(24.0, weather.temperature, 0.0)
        assertEquals("Unknown", weather.condition)
        assertEquals(true, weather.isDay)
    }

    @Test
    fun `validate Unknown weather from entity`() {
        val entity = WeatherEntity(
            city = "Paris",
            temperature = 24.0,
            weatherCode = 4,
            isDay = true,
            cachedAt = System.currentTimeMillis()
        )

        val weather = entity.toDomain()
        assertEquals("Paris", weather.city)
        assertEquals(24.0, weather.temperature, 0.0)
        assertEquals("Unknown", weather.condition)
        assertEquals(true, weather.isDay)
    }
}