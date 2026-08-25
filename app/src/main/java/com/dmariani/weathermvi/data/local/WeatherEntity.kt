package com.dmariani.weathermvi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Weather")
data class WeatherEntity (
    @PrimaryKey val city: String,
    val temperature: Double,
    val weatherCode: Int,
    val isDay: Boolean,
    val cachedAt: Long
)