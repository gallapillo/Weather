package com.gallapillo.weather

import android.annotation.SuppressLint
import android.content.Context

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gallapillo.weather.repository.WeatherRepository
import com.gallapillo.weather.viewmodel.WeatherViewModel
import com.gallapillo.weather.viewmodel.WeatherViewModelFactory
import com.gallapillo.weather.z_utils.COUNTRIES
import com.gallapillo.weather.z_utils.DataConverter.convertSunsetDate
import com.gallapillo.weather.z_utils.PressureConverter.hpaToMmHg
import com.gallapillo.weather.z_utils.TemperatureConverter.kelvinToCel
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherViewModel
    private var isKelvin: Boolean = false
    private var isHpa: Boolean = false
    private var isRound: Boolean = true


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var city = "Novosibirsk"


        loadWeather(city) {
        }

        val cityAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, COUNTRIES)
        ed_choose_city.setAdapter(cityAdapter)

        tv_address.setOnClickListener {
            ll_address_container.visibility = View.INVISIBLE
            ed_choose_city.visibility = View.VISIBLE
            btn_city_add.visibility = View.VISIBLE
        }

        btn_city_add.setOnClickListener {
            city = ed_choose_city.text.toString()
            if (!TextUtils.isEmpty(city)) {
                ll_address_container.visibility = View.VISIBLE
                ed_choose_city.visibility = View.INVISIBLE
                btn_city_add.visibility = View.INVISIBLE
                loadWeather(city) {  }
                hideKeyboard()
            } else {
                Toast.makeText(this, "Please enter your city", Toast.LENGTH_LONG).show()
            }
        }

        tv_temp.setOnLongClickListener {
            isRound = !isRound
            loadWeather(city) {  }
            return@setOnLongClickListener true
        }

        ll_pressure.setOnClickListener {
            isHpa = !isHpa
            loadWeather(city) {  }
        }

        swiperefresh.setOnRefreshListener {
            loadWeather(city) {
                swiperefresh.isRefreshing = false
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadWeather(
        city: String = "Moscow",
        onSuccess: () -> Unit
    ) {
        val repository = WeatherRepository()
        val viewModelFactory = WeatherViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(WeatherViewModel::class.java)

        viewModel.getWeatherFromCity(city)

        viewModel.weather.observe(this, Observer { weatherResponse ->
            if (weatherResponse.isSuccessful) {
                // status
                tv_status.text = weatherResponse.body()?.weather?.get(0)?.main

                // address
                tv_address.text = weatherResponse.body()?.name



                // time
                val sdf = SimpleDateFormat("dd MMMM HH:mm", Locale.ENGLISH)
                val currentDate = sdf.format(Date())

                tv_updated_at.text = "Updated at: $currentDate"
                tv_sunrise.text = weatherResponse.body()?.sys?.sunrise?.let { convertSunsetDate(it) }
                tv_sunset.text = weatherResponse.body()?.sys?.sunset?.let { convertSunsetDate(it) }

                // temp
                val temp = weatherResponse.body()?.main?.temp
                val minTemp = weatherResponse.body()?.main?.temp_min
                val maxTemp = weatherResponse.body()?.main?.temp_max
                if (!isKelvin) {
                    val celTemp = temp?.let { kelvinToCel(it) }
                    val celTempMin = minTemp?.let { kelvinToCel(it) }
                    val celTempMax = maxTemp?.let { kelvinToCel(it) }

                    //
                    celTemp?.let { toggleColorGradientTemperature(it) }
                    //
                    if (isRound) {
                        tv_temp.text = celTemp?.toInt().toString() + "°C"
                        tv_temp_min.text = "Min Temp: " + celTempMin?.toInt().toString() + "°C"
                        tv_temp_max.text = "Max Temp: " + celTempMax?.toInt().toString() + "°C"
                    } else {

                        tv_temp.text = celTemp?.let { BigDecimal(it).setScale(2, RoundingMode.HALF_EVEN).toString() } + "°C"
                        tv_temp_min.text = "Min Temp: " + celTempMin?.let { BigDecimal(it).setScale(2, RoundingMode.HALF_EVEN).toString() } + "°C"
                        tv_temp_max.text = "Max Temp: " + celTempMax?.let { BigDecimal(it).setScale(2, RoundingMode.HALF_EVEN).toString() } + "°C"
                    }

                } else {
                    tv_temp.text = weatherResponse.body()?.main?.temp.toString()
                }

                // pressure
                val pressure = weatherResponse.body()?.main?.pressure
                if (!isHpa) {
                    val mmHgPressure = pressure?.let { hpaToMmHg(pressure) }
                    tv_pressure.text = mmHgPressure?.toInt().toString() + " mmHg"
                } else {
                    tv_pressure.text = pressure?.toInt().toString() + " Hpa"
                }

                // humidity
                val humidity = weatherResponse.body()?.main?.humidity
                tv_humidity.text = humidity.toString() + " %"

                // wind
                val wind = weatherResponse.body()?.wind?.speed
                tv_wind.text = wind.toString() + " m/s"

            } else {
                tv_temp.text = weatherResponse.body().toString()
            }
        })
        onSuccess()
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        imm.hideSoftInputFromWindow(this.window.decorView.windowToken, 0)
    }

    private fun toggleColorGradientTemperature(temp: Double) {

        // Toast.makeText(this, "Color Temp $temp", Toast.LENGTH_LONG).show()
        if (temp < 0) {
          rl_root_container.setBackgroundResource(R.drawable.cold_night_gradient)
        } else if (temp > 0 && temp < 6) {
            // Toast.makeText(this, "Update color starts", Toast.LENGTH_LONG).show()
            rl_root_container.setBackgroundResource(R.drawable.second_gradient)
        } else if (temp > 6 && temp < 12) {
            rl_root_container.setBackgroundResource(R.drawable.pre_warm_night_gradient)
        } else if (temp > 12 && temp < 19) {
            rl_root_container.setBackgroundResource(R.drawable.warm_night_gradient)
        } else if (temp < 26 && temp > 18) {
            rl_root_container.setBackgroundResource(R.drawable.warmer_night_gradient)
        } else if (temp > 26) {
            rl_root_container.setBackgroundResource(R.drawable.hot_night_gradient)
        } else {
            rl_root_container.setBackgroundResource(R.drawable.gradient)
        }
    }
}