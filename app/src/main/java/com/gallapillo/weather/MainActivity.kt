package com.gallapillo.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.TextUtils.isEmpty
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
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherViewModel
    private var isKelvin: Boolean = false
    private var isHpa: Boolean = false


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
                    tv_temp.text = celTemp?.toInt().toString() + "°C"
                    tv_temp_min.text = "Min Temp: " + celTempMin?.toInt().toString() + "°C"
                    tv_temp_max.text = "Max Temp: " + celTempMax?.toInt().toString() + "°C"
                } else {
                    tv_temp.text = weatherResponse.body()?.main?.temp.toString()
                }

                // pressure
                val pressure = weatherResponse.body()?.main?.pressure
                if (!isHpa) {
                    val mmHgPressure = pressure?.let { hpaToMmHg(pressure) }
                    tv_pressure.text = mmHgPressure?.toInt().toString() + " mmHg"
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

    private fun kelvinToCel(temp: Double): Double {
        return temp - 273.15
    }

    private fun hpaToMmHg(pressure: Int): Double {
        return pressure / 1.333
    }

    // for data utils

    private val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.ENGLISH)

    fun testDate(): String {
        val time = 1560507488
        return getDateString(time) // 14 June 2019, 13:18:08
    }

    fun convertSunsetDate(timeStamp: Int): String {
        val sunsetDateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        return sunsetDateFormat.format(timeStamp * 1000L)
    }

    private fun getDateString(time: Long) : String = simpleDateFormat.format(time * 1000L)

    private fun getDateString(time: Int) : String = simpleDateFormat.format(time * 1000L)

    fun hideKeyboard() {
        val imm: InputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        imm.hideSoftInputFromWindow(this.window.decorView.windowToken, 0)
    }
}