package com.example.weatherui.model

data class WeatherForecast(
    val publicTime: String,
    val publicTimeFormatted: String,
    val publishingOffice: String,
    val title: String,
    val link: String,
    val description: Description,
    val forecasts: List<Forecast>,
    val location: Location,
    val copyright: Copyright
) {

    data class Description(
        val publicTime: String,
        val publicTimeFormatted: String,
        val headlineText: String?,
        val bodyText: String
    )


    data class Forecast(
        val date: String,
        val dateLabel: String,
        val telop: String,
        val detail: Detail,
        val temperature: Temperature,
        val chanceOfRain: ChanceOfRain,
        val image: Image
    ) {


        data class Detail(
            val weather: String,
            val wind: String,
            val wave: String
        )


        data class Temperature(
            val min: TemperatureItem?,
            val max: TemperatureItem
        ) {


            data class TemperatureItem(
                val celsius: Float?,
                val fahrenheit: Float?
            )
        }


        data class ChanceOfRain(
            val T00_06: String,
            val T06_12: String,
            val T12_18: String,
            val T18_24: String
        )


        data class Image(
            val title: String,
            val url: String,
            val width: Int,
            val height: Int
        )
    }


    data class Location(
        val area: String,
        val prefecture: String,
        val district: String,
        val city: String
    )


    data class Copyright(
        val title: String,
        val link: String,
        val image: Forecast.Image,
        val provider: List<Provider>
    ) {
        data class Provider(
            val link: String,
            val name: String,
            val note: String
        )
    }
}
