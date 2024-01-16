package com.example.weatherui.ui

import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.weatherui.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var citySpinner: Spinner
    private lateinit var todayTextView: TextView
    private lateinit var tomorrowTextView: TextView
    private lateinit var dayAfterTomorrowTextView: TextView
    private lateinit var indicatorDot: View
    private lateinit var parentLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // installSplashScreen()

        initViews()
        initListeners()
    }

    private fun initViews() {
        citySpinner = findViewById(R.id.citySpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.cities_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            citySpinner.adapter = adapter
        }

        // Find other views using findViewById
        todayTextView = findViewById(R.id.today)
        tomorrowTextView = findViewById(R.id.tomorrow)
        dayAfterTomorrowTextView = findViewById(R.id.day_after_tomorrow)
        indicatorDot = findViewById(R.id.indicator)
        parentLayout = findViewById(R.id.parentLayout)
    }

    private fun initListeners() {
        findViewById<Button>(R.id.searchButton).setOnClickListener {
            changeCityCode()
        }

        todayTextView.setOnClickListener { changeWeatherToDay(R.id.today) }
        tomorrowTextView.setOnClickListener { changeWeatherToDay(R.id.tomorrow) }
        dayAfterTomorrowTextView.setOnClickListener { changeWeatherToDay(R.id.day_after_tomorrow) }
    }

    private fun changeCityCode() {
        val cityId = getCityIdFromSpinner()
        val cityCodeUrl = buildCityCodeUrl(cityId)
        weatherRequestTask(cityCodeUrl)
    }

    private fun getCityIdFromSpinner(): Int {
        val selectedCity = citySpinner.selectedItem as String
        return when (selectedCity) {
            "東京" -> 130010
            "名古屋" -> 230010
            "大阪" -> 270000
            else -> 0
        }
    }

    private fun buildCityCodeUrl(cityId: Int): String {
        val mainUrl = "http://weather.tsukumijima.net/api/forecast?city="
        return mainUrl + cityId
    }

    private fun weatherRequestTask(cityCodeUrl: String) {
        lifecycleScope.launch {
            val result = weatherBackgroundTask(cityCodeUrl)

            cityCodeJsonTask(result)
        }
    }

    private suspend fun weatherBackgroundTask(cityCodeUrl: String): String {
        val response = withContext(Dispatchers.IO) {
            var httpResult = ""

            try {
                val urlObj = URL(cityCodeUrl)
                val br = BufferedReader(InputStreamReader(urlObj.openStream()))

                httpResult = br.readText()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return@withContext httpResult
        }

        return response
    }

    private fun cityCodeJsonTask(result: String) {
        val title = findViewById<TextView>(R.id.apiTitle)
        val description = findViewById<TextView>(R.id.weatherDescription)
        val weather = findViewById<TextView>(R.id.weatherType)
        val weatherImage = findViewById<ImageView>(R.id.weatherIcon)

        val jsonObj = JSONObject(result)

        title.text = jsonObj.getString("title")
        description.text = jsonObj.getJSONObject("description").getString("bodyText")
        weather.text = jsonObj.getJSONArray("forecasts").getJSONObject(0).getJSONObject("detail").getString("weather")

        // 画像の URL を取得
        val imageUrl = jsonObj.getJSONArray("forecasts").getJSONObject(0).getJSONObject("image").getString("url")
        // URL から画像データを取得
        val inputStream = URL(imageUrl).openStream()
        val bitmap = BitmapFactory.decodeStream(inputStream)
        // ImageView に画像を設定
        weatherImage.setImageBitmap(bitmap)
    }

    private fun changeWeatherToDay(viewId: Int) {
        setActiveTextColor(viewId)
        changeIndicatorDotPosition(viewId)
    }

    private fun setActiveTextColor(activeViewId: Int) {
        val activeColor = getColor(R.color.textColor)
        val inactiveColor = Color.parseColor("#D6996B")
        listOf(todayTextView, tomorrowTextView, dayAfterTomorrowTextView).forEach {
            it.setTextColor(if (it.id == activeViewId) activeColor else inactiveColor)
        }
    }

    private fun changeIndicatorDotPosition(viewId: Int) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(parentLayout)
        constraintSet.connect(R.id.indicator, ConstraintSet.START, viewId, ConstraintSet.START, 0)
        constraintSet.connect(R.id.indicator, ConstraintSet.END, viewId, ConstraintSet.END, 0)
        constraintSet.applyTo(parentLayout)
    }
}