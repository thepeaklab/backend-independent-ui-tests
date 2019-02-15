package com.thepeaklab.backendindependentuitests.feature.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.intent.rule.IntentsTestRule
import com.thepeaklab.backendindependentuitests.R
import com.thepeaklab.backendindependentuitests.core.mock.Expectation
import com.thepeaklab.backendindependentuitests.core.mock.MockApiService
import com.thepeaklab.backendindependentuitests.core.mock.MockFailure
import com.thepeaklab.backendindependentuitests.core.mock.MockSuccess
import com.thepeaklab.backendindependentuitests.feature.main.view.MainActivity
import com.thepeaklab.backendindependentuitests.utils.checkIsDisplayedAsText
import com.thepeaklab.backendindependentuitests.utils.performClick
import kotlinx.android.synthetic.main.activity_main.view.*
import org.json.JSONArray
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class MainViewTest {

    @Rule
    @JvmField
    var testRule = IntentsTestRule(MainActivity::class.java, true, false)

    // needed for correct handling of 'postValue' in LiveData
    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {

        // reset expectations so it's clean on new test
        MockApiService.resetExpectation()
    }

    @Test
    fun request_data___success___response_is_shown() {

        // define test data
        val myArray = JSONArray()
        myArray.put("the test response data")

        // set expectation
        MockApiService.setExpectation(MockApiService.Method.getData, Expectation(success = { MockSuccess(myArray) }))

        // launch activity
        testRule.launchActivity(null)

        // trigger action
        R.id.button.performClick()

        // check assertions
        "the test response data".checkIsDisplayedAsText()
    }

    @Test
    fun request_data___failure___no_data_text_is_shown() {

        // set expectation
        MockApiService.setExpectation(MockApiService.Method.getData, Expectation(failure = { MockFailure("some error") }))

        // launch activity
        testRule.launchActivity(null)

        // trigger action
        R.id.button.performClick()

        // check assertions
        "- no data -".checkIsDisplayedAsText()
    }
}