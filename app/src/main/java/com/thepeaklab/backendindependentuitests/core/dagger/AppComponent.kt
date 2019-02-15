package com.thepeaklab.backendindependentuitests.core.dagger

import com.thepeaklab.backendindependentuitests.feature.main.viewmodel.MainViewModel
import dagger.Component
import javax.inject.Singleton

@Component(modules = arrayOf(ApiModule::class))
@Singleton
interface AppComponent {

    fun inject(viewModel: MainViewModel)
}