package com.gallapillo.weather.z_utils

object TemperatureConverter {
    fun kelvinToCel(temp: Double): Double {
        return temp - 273.15
    }
}