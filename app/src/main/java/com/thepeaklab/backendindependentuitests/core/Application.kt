package com.thepeaklab.backendindependentuitests.core

import android.app.Application
import com.androidnetworking.AndroidNetworking
import com.jacksonandroidnetworking.JacksonParserFactory
import com.thepeaklab.backendindependentuitests.core.dagger.Injector
import okhttp3.OkHttpClient

open class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        Injector.INSTANCE.init(this)

        AndroidNetworking.initialize(applicationContext)

        val okHttpClient = OkHttpClient().newBuilder()
            .build()

        AndroidNetworking.initialize(applicationContext, okHttpClient)

        // set JacksonParserFactory like below
        AndroidNetworking.setParserFactory(JacksonParserFactory())
    }
}