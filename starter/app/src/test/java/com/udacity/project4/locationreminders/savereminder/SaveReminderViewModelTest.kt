package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    // Subject under test
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    // Use a fake data source to be injected into the viewmodel
    private lateinit var data: FakeDataSource

    private val golden_gate_bridge_reminder = ReminderDTO(
        "Practice diving",
        "Improve salto",
        "Golden Gate Bridge",
        37.8199,
        122.4786,
        "1"
    )
    private val arctic_reminder =
        ReminderDTO("Pet polar bear", "Take snow boots with you", "Arctic", 76.2506, 100.1140, "2")
    private val mount_etna_reminder =
        ReminderDTO("Go for a sauna", "Don't forget towel", "Mount Etna", 37.7510, 14.9934, "3")

    //For unit testing, set the primary coroutine dispatcher.
    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUpViewModel() {
        stopKoin()
        data = FakeDataSource()
        val context = ApplicationProvider.getApplicationContext<Application>()
        saveReminderViewModel = SaveReminderViewModel(context, data)
    }

    @After
    fun clearData() = runTest {
        data.deleteAllReminders()
    }


}