package com.gallapillo.weather.z_utils

object PressureConverter {
    fun hpaToMmHg(pressure: Int): Double = pressure / 1.333
}