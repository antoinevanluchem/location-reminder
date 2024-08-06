package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel
import org.mockito.Mockito.mock
import kotlin.test.Test
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.CoreMatchers.not
import org.mockito.Mockito.verify


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : KoinTest {

    private val dataSource: ReminderDataSource by inject()

    private val goldenGateBridgeReminder = ReminderDTO(
        "Practice diving", "Improve salto", "Golden Gate Bridge", 37.8199, 122.4786, "1"
    )
    private val arcticReminder =
        ReminderDTO("Pet polar bear", "Take snow boots with you", "Arctic", 76.2506, 100.1140, "2")
    private val mountEtnaReminder =
        ReminderDTO("Go for a sauna", "Don't forget towel", "Mount Etna", 37.7510, 14.9934, "3")

    @Before
    fun initRepository() {
        stopKoin()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    get(), get()
                )
            }
            single {
                FakeDataSource() as ReminderDataSource
            }
        }
        startKoin {
            androidContext(getApplicationContext())
            modules(listOf(myModule))
        }
    }

    @After
    fun deleteAllReminders() = runBlocking { dataSource.deleteAllReminders() }

    @Test
    fun savedRemindersAreDisplayed() = runTest {
        dataSource.saveReminder(goldenGateBridgeReminder)
        dataSource.saveReminder(arcticReminder)
        dataSource.saveReminder(mountEtnaReminder)

        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }

        onView(withText(goldenGateBridgeReminder.title)).check(matches(isDisplayed()))
        onView(withText(arcticReminder.description)).check(matches(isDisplayed()))
        onView(withText(mountEtnaReminder.title)).check(matches(isDisplayed()))

        onView(withId(R.id.noDataTextView)).check(matches(not(isDisplayed())))
    }

    @Test
    fun noDataTextViewDisplayed() = runTest {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }

        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
        onView(withText(R.string.no_data)).check(matches(isDisplayed()))

        onView(withText(goldenGateBridgeReminder.title)).check(doesNotExist())
        onView(withText(arcticReminder.description)).check(doesNotExist())
        onView(withText(mountEtnaReminder.title)).check(doesNotExist())
    }


    @Test
    fun clickButtonAndNavigate() = runTest {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }

        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }
}