package com.thepeaklab.backendindependentuitests.feature.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.androidnetworking.error.ANError
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.whenever
import com.thepeaklab.backendindependentuitests.common.data.ApiService
import com.thepeaklab.backendindependentuitests.core.dagger.ApiModule
import com.thepeaklab.backendindependentuitests.core.dagger.DaggerAppComponent
import com.thepeaklab.backendindependentuitests.core.dagger.Injector
import com.thepeaklab.backendindependentuitests.feature.main.viewmodel.MainViewModel
import org.json.JSONArray
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

/**
 * MainViewModelTest.kt
 */
class MainViewModelTest {

    private lateinit var apiService: ApiService

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {

        // mock service
        apiService = mock()

        // define dagger dependencies
        val appComponent = DaggerAppComponent.builder()
            .apiModule(object : ApiModule() {
                override fun provideApiService(): ApiService {
                    return apiService
                }
            })
            .build()

        // set appComponent
        Injector.INSTANCE.set(appComponent)
    }

    @After
    fun tearDown() {

        // clear dependencies
        Injector.INSTANCE.clear()
    }

    @Test
    fun `get response data | apiService is requested`() {

        // init viewModel
        val viewModel = MainViewModel()

        // trigger action
        viewModel.loadData()

        // check assertions
        verify(apiService, times(1)).getData(any<(JSONArray) -> Unit>(), any<(ANError) -> Unit>())
    }

    @Test
    fun `get response data | success | response is returned`() {

        // define mock behavior
        doAnswer {

            val myArray = JSONArray()
            myArray.put("test")
            val successLambda = it.arguments[0] as (JSONArray) -> Unit
            successLambda(myArray)
            null
        }.whenever(apiService).getData(any<(JSONArray) -> Unit>(), any())

        // init viewModel
        val viewModel = MainViewModel()

        // trigger action
        viewModel.loadData()

        // check assertions
        assertEquals("test", viewModel.data.value)
    }

    @Test
    fun `get response data | error | '-no data -' is returned`() {

        // define mock behavior
        doAnswer {
            val errorLambda = it.arguments[1] as (ANError) -> Unit
            errorLambda.invoke(ANError("test message"))
        }.whenever(apiService).getData(any(), any<(ANError) -> Unit>())

        // init viewModel
        val viewModel = MainViewModel()

        // trigge action
        viewModel.loadData()

        // check assertions
        assertEquals("- no data -", viewModel.data.value)
    }
}