package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.Result.Success
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext.stopKoin


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    // Subject under test
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    // Use a fake data source to be injected into the viewmodel
    private lateinit var fakeDataSource: FakeDataSource

    //For unit testing, set the primary coroutine dispatcher.
    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Context
    private val context: Application = ApplicationProvider.getApplicationContext<Application>()

    @Before
    fun setUpViewModel() {
        stopKoin()
        fakeDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(context, fakeDataSource)
    }

    @After
    fun clearData() = runTest {
        fakeDataSource.deleteAllReminders()
    }

    @Test
    fun validateAndSaveReminder_WhenReminderTitleIsEmpty_ThenDoNotSaveReminder() {
        val emptyTitleReminder =
            ReminderDataItem("", "Take snow boots with you", "Arctic", 76.2506, 100.1140, "2")

        saveReminderViewModel.validateAndSaveReminder(emptyTitleReminder)
        coroutineRule.testScheduler.runCurrent()

        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            `is`(R.string.err_enter_title)
        )
    }

    @Test
    fun validateAndSaveReminder_WhenReminderTitleIsNull_ThenDoNotSaveReminder() {
        val nullTitleReminder =
            ReminderDataItem(null, "Take snow boots with you", "Arctic", 76.2506, 100.1140, "2")

        saveReminderViewModel.validateAndSaveReminder(nullTitleReminder)
        coroutineRule.testScheduler.runCurrent()

        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            `is`(R.string.err_enter_title)
        )
    }

    @Test
    fun validateAndSaveReminder_WhenReminderLocationIsEmpty_ThenDoNotSaveReminder() {
        val emptyLocationReminder = ReminderDataItem(
            "Pet polar bear",
            "Take snow boots with you",
            "",
            76.2506,
            100.1140,
            "2"
        )

        saveReminderViewModel.validateAndSaveReminder(emptyLocationReminder)
        coroutineRule.testScheduler.runCurrent()

        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            `is`(R.string.err_select_location)
        )
    }

    @Test
    fun validateAndSaveReminder_WhenReminderLocationIsNull_ThenDoNotSaveReminder() {
        val nullLocationReminder = ReminderDataItem(
            "Pet polar bear",
            "Take snow boots with you",
            null,
            76.2506,
            100.1140,
            "2"
        )

        saveReminderViewModel.validateAndSaveReminder(nullLocationReminder)
        coroutineRule.testScheduler.runCurrent()

        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            `is`(R.string.err_select_location)
        )
    }

    @Test
    fun validateAndSaveReminder_WhenReminderIsValid_ThenSaveReminder() = runTest {
        val reminderDataItem = ReminderDataItem(
            "Pet polar bear",
            "Take snow boots with you",
            "Arctic",
            76.2506,
            100.1140,
            "2"
        )

        saveReminderViewModel.validateAndSaveReminder(reminderDataItem)
        coroutineRule.testScheduler.runCurrent()

        assertThat(
            saveReminderViewModel.showToast.getOrAwaitValue(),
            `is`(context.getString(R.string.reminder_saved))
        )

        val reminderDTO = (fakeDataSource.getReminders() as Success).data[0]
        assertThat(reminderDTO.title, `is`(reminderDataItem.title))
        assertThat(reminderDTO.description, `is`(reminderDataItem.description))
        assertThat(reminderDTO.location, `is`(reminderDataItem.location))
        assertThat(reminderDTO.latitude, `is`(reminderDataItem.latitude))
        assertThat(reminderDTO.longitude, `is`(reminderDataItem.longitude))
        assertThat(reminderDTO.id, `is`(reminderDataItem.id))
    }

}