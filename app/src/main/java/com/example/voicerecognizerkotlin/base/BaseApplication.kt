package com.example.voicerecognizerkotlin.base

import android.app.Application
import android.content.Context
import com.example.voicerecognizerkotlin.DaggerWeatherComponent

object BaseApplication :Application(){
    var context: Context ?= null
    override fun onCreate() {
        context = this
        super.onCreate()



    }


}