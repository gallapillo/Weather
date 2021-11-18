package com.gallapillo.weather.api

import com.gallapillo.weather.model.Weather
import com.gallapillo.weather.z_utils.API_KEY
import com.gallapillo.weather.z_utils.Constants.BASE_URL
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherAPI {

    @GET("weather")
    suspend fun getWeatherFromCity(
        @Query("q") q : String,
        @Query("appid") appid: String = API_KEY
    ) : Response<Weather>
}