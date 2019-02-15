package com.thepeaklab.backendindependentuitests.common.data

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import org.json.JSONArray

class ApiServiceImpl : ApiService {

    override fun getData(onResponse: (response: JSONArray) -> Unit, onError: (error: ANError) -> Unit) {
        AndroidNetworking.get("https://jsonplaceholder.typicode.com/posts")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray) {
                    onResponse(response)
                }

                override fun onError(error: ANError) {
                    onError(error)
                }
            })
    }
}