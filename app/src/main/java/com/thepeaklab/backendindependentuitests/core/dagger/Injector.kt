package com.thepeaklab.backendindependentuitests.core.dagger

import android.app.Application

enum class Injector {
    INSTANCE;

    // private fields
    /**
     * get generated app component
     *
     * @return
     */
    internal var appComponent: AppComponent? = null

    /**
     * init method
     *
     */
    fun init(application: Application) {
        appComponent = DaggerAppComponent.builder()
            .apiModule(ApiModule())
            .build()
    }

    /**
     * get app component
     *
     */
    fun get(): AppComponent {
        return appComponent!!
    }

    /**
     * set app component
     *
     * @param appComponent
     */
    fun set(appComponent: AppComponent) {
        this.appComponent = appComponent
    }

    /**
     * clear
     *
     */
    fun clear() {
        this.appComponent = null
    }
}