package com.dmariani.weathermvi.data.remote

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("current") val current: CurrentWeatherResponse
)

data class CurrentWeatherResponse (
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("is_day") val isDay: Int
)