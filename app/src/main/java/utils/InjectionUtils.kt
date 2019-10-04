package utils

import android.content.Context
import data.WeatherRepository

object InjectionUtils {
     fun injectWeatherRepository(context: Context)  = WeatherRepository(context)
}