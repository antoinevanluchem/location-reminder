package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
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

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    // Subject under test
    private lateinit var remindersList: RemindersListViewModel

    // Use a fake data source to be injected into the viewmodel
    private lateinit var fakeDataSource: FakeDataSource

    private val goldenGateBridgeReminder = ReminderDTO(
        "Practice diving",
        "Improve salto",
        "Golden Gate Bridge",
        37.8199,
        122.4786,
        "1"
    )
    private val arcticReminder =
        ReminderDTO("Pet polar bear", "Take snow boots with you", "Arctic", 76.2506, 100.1140, "2")
    private val mountEtnaReminder =
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
        fakeDataSource = FakeDataSource()
        remindersList = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @After
    fun clearData() = runTest {
        fakeDataSource.deleteAllReminders()
    }

    @Test
    fun loadReminders_WhenEmptyRemindersList_ThenSizeIsZeroAndShowNoDataIsTrue() {
        remindersList.loadReminders()
        coroutineRule.testScheduler.runCurrent()

        assertThat(remindersList.remindersList.getOrAwaitValue().size, `is`(0))
        assertThat(remindersList.showNoData.getOrAwaitValue(), `is`(true))
    }

    @Test
    fun loadReminders_WhenRemindersAddedToRemindersList_ThenHaveCorrectSizeAndShowNoDateIsFalse() = runTest {
        fakeDataSource.saveReminder(goldenGateBridgeReminder)
        fakeDataSource.saveReminder(arcticReminder)
        fakeDataSource.saveReminder(mountEtnaReminder)

        remindersList.loadReminders()
        coroutineRule.testScheduler.runCurrent()


        assertThat(remindersList.remindersList.getOrAwaitValue().size, `is`(3))
        assertThat(remindersList.showNoData.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun loadReminders_WhenGetRemindersHasError_ThenShowSnackbarMessage() {
        fakeDataSource.setReturnError(true)

        remindersList.loadReminders()
        coroutineRule.testScheduler.runCurrent()

        assertThat(
            remindersList.showSnackBar.getOrAwaitValue(),
            `is`("shouldReturnError was set to true in the FakeDataSource")
        )
    }

    @Test
    fun loadReminders_WhenGettingReminders_ThenShowLoadingIsTrue() {
        coroutineRule.testScheduler.advanceUntilIdle()

        remindersList.loadReminders()

        assertThat(remindersList.showLoading.getOrAwaitValue(), `is`(true))

        coroutineRule.testScheduler.runCurrent()

        assertThat(remindersList.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(remindersList.showNoData.getOrAwaitValue(), `is`(true))
    }
}