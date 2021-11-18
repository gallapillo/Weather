package com.gallapillo.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gallapillo.weather.model.Weather
import com.gallapillo.weather.repository.WeatherRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModel(private val repository: WeatherRepository): ViewModel() {

    val weather: MutableLiveData<Response<Weather>> = MutableLiveData()

    fun getWeatherFromCity(city: String = "Moscow") {
        viewModelScope.launch {
            val response = repository.getWeatherFromCity(city)
            weather.value = response
        }
    }
}