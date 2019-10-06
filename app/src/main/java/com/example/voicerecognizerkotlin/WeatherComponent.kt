package com.example.voicerecognizerkotlin

import com.example.voicerecognizerkotlin.data.WeatherRepository
import com.example.voicerecognizerkotlin.ui.WeatherViewModel
import dagger.Component
@Component(modules = [WeatherModule::class])
interface WeatherComponent {
    fun injectRepository(): WeatherRepository
    fun injectWeatherViewModel():WeatherViewModel
}