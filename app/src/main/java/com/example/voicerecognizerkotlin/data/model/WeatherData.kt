package com.example.voicerecognizerkotlin.data.model


data class WeatherData (

    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main?,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Int,
    val sys: Sys,
    val id: Int,
    val name: String,
    val cod: Int

)




data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Wind(

    val speed: Double,
    val deg: Int
)

data class Sys(

    val message: Double,
    val country: String,
    val sunrise: Int,
    val sunset: Int
)

data class Main(

    val temp: Double,
    val pressure: Double,
    val humidity: Int,
    val temp_min: Double,
    val temp_max: Double,
    val sea_level: Double,
    val grnd_level: Double
)

data class Coord(

    val lon: Double,
    val lat: Double
)

data class Clouds(

    val all: Int
)