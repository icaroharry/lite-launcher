package com.litelauncher.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.litelauncher.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Test
    fun mainScreen_displaysSearchInput() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.searchInput)).check(matches(isDisplayed()))
    }

    @Test
    fun mainScreen_displaysFavorites() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.favoritesRecycler)).check(matches(isDisplayed()))
    }

    @Test
    fun mainScreen_displaysAppsList() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.appsRecycler)).check(matches(isDisplayed()))
    }

    @Test
    fun mainScreen_displaysSettingsButton() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withText(R.string.settings)).check(matches(isDisplayed()))
    }
}
