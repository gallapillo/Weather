package com.gallapillo.weather.z_utils

import java.text.SimpleDateFormat
import java.util.*

object DataConverter {
    private val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.ENGLISH)

    fun convertSunsetDate(timeStamp: Int): String {
        val sunsetDateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        return sunsetDateFormat.format(timeStamp * 1000L)
    }

    fun getDateString(time: Long) : String = simpleDateFormat.format(time * 1000L)

    fun getDateString(time: Int) : String = simpleDateFormat.format(time * 1000L)
}