package com.thepeaklab.backendindependentuitests.utils

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers

fun String.checkIsDisplayedAsText() = Espresso.onView(ViewMatchers.withText(this)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
fun Int.performClick() = Espresso.onView(ViewMatchers.withId(this)).perform(ViewActions.click())