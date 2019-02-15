package com.thepeaklab.backendindependentuitests.core

import com.thepeaklab.backendindependentuitests.common.data.ApiService
import com.thepeaklab.backendindependentuitests.core.dagger.ApiModule
import com.thepeaklab.backendindependentuitests.core.dagger.DaggerAppComponent
import com.thepeaklab.backendindependentuitests.core.dagger.Injector
import com.thepeaklab.backendindependentuitests.core.mock.MockApiService

class TestApplication : Application() {

    // initialize mock ApiService
    var mockApiService = MockApiService

    /**
     * onCreate
     *
     */
    override fun onCreate() {
        super.onCreate()

        // init dependency injection
        val appComponent = DaggerAppComponent.builder()
            .apiModule(object : ApiModule() {
                override fun provideApiService(): ApiService {
                    return mockApiService.service
                }
            })
            .build()

        // set AppComponent
        Injector.INSTANCE.set(appComponent)
    }
}
