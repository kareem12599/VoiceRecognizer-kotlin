package com.example.voicerecognizerkotlin.utils

import android.content.Context
import com.example.voicerecognizerkotlin.data.WeatherRepository

object InjectionUtils {
     fun injectWeatherRepository(context: Context)  = WeatherRepository(context)
}