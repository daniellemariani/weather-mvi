package com.dmariani.weathermvi.data

import com.dmariani.weathermvi.data.local.WeatherEntity
import com.dmariani.weathermvi.data.remote.WeatherResponse
import com.dmariani.weathermvi.domain.model.Weather

private fun weatherCodeToString(code: Int): String {
    return when (code) {
        0 -> "Clear"
        1, 2, 3 -> "Cloudy"
        45, 48 -> "Fog"
        51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> "Rain"
        95, 96, 99 -> "Storm"
        71, 73, 75, 77, 85, 86 -> "Snow"
        else -> "Unknown"
    }
}

fun WeatherResponse.toDomain(city: String): Weather {
    return Weather(
        city = city,
        temperature = current.temperature,
        condition = weatherCodeToString(current.weatherCode),
        isDay = current.isDay == 1
    )
}

fun WeatherResponse.toEntity(city: String, timestamp: Long): WeatherEntity {
    return WeatherEntity(
        city = city,
        temperature = current.temperature,
        weatherCode = current.weatherCode,
        isDay = current.isDay == 1,
        cachedAt = timestamp
    )
}

fun WeatherEntity.toDomain(): Weather {
    return Weather(
        city = city,
        temperature = temperature,
        condition = weatherCodeToString(weatherCode),
        isDay = isDay
    )
}