package com.example.voicerecognizerkotlin

import android.content.Context
import com.example.voicerecognizerkotlin.base.BaseApplication
import dagger.Module
import dagger.Provides


@Module
class WeatherModule(private val context: Context){

    @Provides
    fun providesContext(): Context {
        return context
    }
}