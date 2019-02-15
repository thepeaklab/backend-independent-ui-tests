package com.thepeaklab.backendindependentuitests.common.data

import com.androidnetworking.error.ANError
import org.json.JSONArray

interface ApiService {

    // get data from rest server
    fun getData(onResponse: (response: JSONArray) -> Unit, onError: (error: ANError) -> Unit)
}