package data.network

import constants.Constants
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

       val weatherApi: WeatherApi by lazy {
           Retrofit.Builder()
               .baseUrl(Constants.BASE_URL)
               .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
               .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
               .addConverterFactory(GsonConverterFactory.create())
               .build().create(WeatherApi::class.java)
       }


}