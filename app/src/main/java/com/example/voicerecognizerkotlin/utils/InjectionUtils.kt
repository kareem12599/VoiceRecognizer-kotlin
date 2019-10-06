package com.example.voicerecognizerkotlin.utils

import android.content.Context
import com.example.voicerecognizerkotlin.DaggerWeatherComponent
import com.example.voicerecognizerkotlin.WeatherComponent
import com.example.voicerecognizerkotlin.WeatherModule
import javax.inject.Inject

object InjectionUtils {

    fun injectWeatherRepository(context: Context) =
        createWeatherComponent(context).injectRepository()

    fun injectViewModel(context: Context) =
        createWeatherComponent(context).injectWeatherViewModel()

    private fun createWeatherComponent(context: Context): WeatherComponent {
        return DaggerWeatherComponent.builder().weatherModule(WeatherModule(context)).build()
    }

}


