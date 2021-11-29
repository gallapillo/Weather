package com.gallapillo.weather.z_utils

object PressureConverter {
    fun hpaToMmHg(pressure: Int): Double {
        return pressure / 1.333
    }
}