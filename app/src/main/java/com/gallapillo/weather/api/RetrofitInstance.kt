package com.gallapillo.weather.api

import com.gallapillo.weather.z_utils.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: WeatherAPI by lazy {
        retrofit.create(WeatherAPI::class.java)
    }
}