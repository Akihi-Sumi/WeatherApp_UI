package com.example.weatherui.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.weatherui.R
import com.example.weatherui.databinding.ActivityMainBinding
import com.example.weatherui.model.Weather
import com.example.weatherui.utils.Utils
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val client = OkHttpClient.Builder()
        .connectTimeout(100000, TimeUnit.MILLISECONDS)
        .readTimeout(100000.toLong(), TimeUnit.MILLISECONDS)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentDate = Date()
        val dateFormat = SimpleDateFormat("MMM/d(EEE)", Locale.getDefault())
        binding.today.text = dateFormat.format(currentDate)

        // installSplashScreen()

        initViews()
        initListeners()
    }

    private fun initViews() {
        ArrayAdapter.createFromResource(
            this,
            R.array.cities_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.citySpinner.adapter = adapter
        }
    }

    private fun initListeners() {
        binding.searchButton.setOnClickListener {
            requestCityWeather()
        }

//        binding.today.setOnClickListener { changeWeatherToDay(R.id.today) }
//        binding.tomorrow.setOnClickListener { changeWeatherToDay(R.id.tomorrow) }
//        binding.dayAfterTomorrow.setOnClickListener { changeWeatherToDay(R.id.day_after_tomorrow) }
    }

    private fun requestCityWeather() {
        val cityId = getCityIdFromSpinner()
        val requestUrl = "https://weather.tsukumijima.net/api/forecast/city/$cityId"

        Thread {
            val handler = Handler(Looper.getMainLooper())
            val responseBody = startGetRequest(requestUrl)

            if (responseBody != null) {
                val weather = Json.decodeFromString<Weather>(responseBody)

                val weatherTitle = weather.title
                val weatherTodayType = weather.forecasts?.get(0)?.detail?.weather
                val weatherTodayIcon = weather.forecasts?.get(0)?.image?.url
                val weatherTomorrowType = weather.forecasts?.get(1)?.detail?.weather
                val weatherTomorrowIcon = weather.forecasts?.get(1)?.image?.url
                val weatherDatType = weather.forecasts?.get(2)?.detail?.weather
                val weatherDatIcon = weather.forecasts?.get(2)?.image?.url
                val weatherText = weather.description?.text
                val weatherWind = weather.forecasts?.get(0)?.detail?.wind

                if (weatherTodayIcon != null && weatherTomorrowIcon != null && weatherDatIcon != null) {
                    Utils().fetchSVG(this, weatherTodayIcon, binding.weatherTodayIcon)
                    Utils().fetchSVG(this, weatherTomorrowIcon, binding.weatherTomorrowIcon)
                    Utils().fetchSVG(this, weatherDatIcon, binding.weatherDatIcon)
                }

                handler.post {
                    binding.weatherTitle.text = weatherTitle
                    binding.weatherTodayType.text = weatherTodayType
                    binding.weatherTodayType.text = weatherTomorrowType
                    binding.datWeatherDatType.text = weatherDatType
//                    binding.weatherText.text = weatherText
//                    binding.weatherWind.text = weatherWind
                }

            }
        }.start()
    }

    private fun getCityIdFromSpinner(): Int {
        val selectedCity = binding.citySpinner.selectedItem as String
        return when (selectedCity) {
            "東京" -> 130010
            "名古屋" -> 230010
            "大阪" -> 270000
            else -> 0
        }
    }

    @Throws(IOException::class)
    private fun startGetRequest(url: String): String? {
        val request: Request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            return response.body?.string()
        }
    }

//    private fun changeWeatherToDay(viewId: Int) {
//        setActiveTextColor(viewId)
//        changeIndicatorDotPosition(viewId)
//    }

//    private fun setActiveTextColor(activeViewId: Int) {
//        val activeColor = getColor(R.color.textColor)
//        val inactiveColor = Color.parseColor("#D6996B")
//        listOf(binding.today, binding.tomorrow, binding.dayAfterTomorrow).forEach {
//            it.setTextColor(if (it.id == activeViewId) activeColor else inactiveColor)
//        }
//    }

//    private fun changeIndicatorDotPosition(viewId: Int) {
//        val constraintSet = ConstraintSet()
//        constraintSet.clone(binding.parentLayout)
//        constraintSet.connect(R.id.indicator, ConstraintSet.START, viewId, ConstraintSet.START, 0)
//        constraintSet.connect(R.id.indicator, ConstraintSet.END, viewId, ConstraintSet.END, 0)
//        constraintSet.applyTo(binding.parentLayout)
//    }
}