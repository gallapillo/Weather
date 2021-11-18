package com.gallapillo.weather.repository

import android.app.Application
import com.gallapillo.weather.api.RetrofitInstance
import com.gallapillo.weather.model.Weather
import retrofit2.Response

class WeatherRepository {

    suspend fun getWeatherFromCity(city: String = "Moscow"): Response<Weather> {
        return RetrofitInstance.api.getWeatherFromCity(city)
    }
}