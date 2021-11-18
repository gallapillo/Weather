package com.gallapillo.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gallapillo.weather.model.Weather
import com.gallapillo.weather.repository.WeatherRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModelFactory(
    private val repository: WeatherRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WeatherViewModel(repository) as T
    }

}