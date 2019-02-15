package com.thepeaklab.backendindependentuitests.core.dagger

import com.thepeaklab.backendindependentuitests.common.data.ApiService
import com.thepeaklab.backendindependentuitests.common.data.ApiServiceImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class ApiModule {

    @Provides
    @Singleton
    open fun provideApiService(): ApiService {
        return ApiServiceImpl()
    }
}