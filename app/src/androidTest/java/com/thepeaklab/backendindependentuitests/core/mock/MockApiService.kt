package com.thepeaklab.backendindependentuitests.core.mock

import com.androidnetworking.error.ANError
import com.thepeaklab.backendindependentuitests.common.data.ApiService
import org.json.JSONArray

object MockApiService {

    // all available methods
    enum class Method {
        getData
    }

    // expectations
    private val expectationMap = mutableMapOf<Method, Expectation>()

    /**
     * set expectation
     *
     */
    fun setExpectation(method: Method, expectedResult: Expectation) {
        expectationMap.put(method, expectedResult)
    }

    /**
     * reset all expectation so the default will be used
     *
     */
    fun resetExpectation() {
        expectationMap.clear()
    }

    /**
     * hold repository object
     * and return mocked data
     *
     */
    val service = object : ApiService {

        override fun getData(onResponse: (response: JSONArray) -> Unit, onError: (error: ANError) -> Unit) {
            expectationMap[Method.getData]?.let {
                it.success?.let {
                    val mockSuccess = it.invoke()
                    onResponse(mockSuccess.result as JSONArray)
                    return
                }
                it.failure?.let {
                    val mockFailure = it.invoke()
                    onError(ANError(mockFailure.error))
                    return
                }
            }

            val myArray = JSONArray()
            myArray.put("test")

            return onResponse(myArray)
        }
    }
}