package com.thepeaklab.backendindependentuitests.feature.main.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thepeaklab.backendindependentuitests.common.data.ApiService
import com.thepeaklab.backendindependentuitests.core.dagger.Injector
import javax.inject.Inject

class MainViewModel : ViewModel() {

    @Inject
    lateinit var apiService: ApiService

    val data : MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    init {

        data.value = ""

        // inject dependencies
        Injector.INSTANCE.get().inject(this)
    }

    fun loadData() {
        apiService.getData(
            onResponse = {
                val sb = StringBuffer()
                for (i in 0 until it.length()) {
                    sb.append(it.getString(0))
                }
                data.postValue(sb.toString())
            },
            onError = {
                data.postValue("- no data -")
                Log.e("", "error occurred")
            }
        )
    }
}