package com.bigblackwolf.apps.telepush.data.network.api

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
private val instance: Retrofit
    get(){
        return Retrofit.Builder()
                // UNIVERSITY
                //.baseUrl("http://192.168.1.5/api/")
                // JOB
                //.baseUrl("http://192.168.19.122/api/")
                // JULIA
                //.baseUrl("http://192.168.0.104/api/")
                //HOME
                .baseUrl("http://192.168.1.5/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }
    fun getApi() : ITelepushApi
    {
        return instance.create(ITelepushApi::class.java)
    }
}
