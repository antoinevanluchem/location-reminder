package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {
    private lateinit var repository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    private val goldenGateBridgeReminder = ReminderDTO(
        "Practice diving", "Improve salto", "Golden Gate Bridge", 37.8199, 122.4786, "1"
    )
    private val arcticReminder =
        ReminderDTO("Pet polar bear", "Take snow boots with you", "Arctic", 76.2506, 100.1140, "2")
    private val mountEtnaReminder =
        ReminderDTO("Go for a sauna", "Don't forget towel", "Mount Etna", 37.7510, 14.9934, "3")


    // use Architecture Components to concurrently carry out each job.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initial() {
        // testing with an in-memory database because it won't survive stopping the process
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun closeDb() = database.close()


    @Test
    fun saveReminderAndGetReminder() = runTest {
        repository.saveReminder(goldenGateBridgeReminder)

        val result = repository.getReminder(goldenGateBridgeReminder.id)

        assertThat(result as Result.Success, notNullValue())
        assertThat(result.data.title, `is`(goldenGateBridgeReminder.title))
        assertThat(result.data.description, `is`(goldenGateBridgeReminder.description))
        assertThat(result.data.location, `is`(goldenGateBridgeReminder.location))
        assertThat(result.data.latitude, `is`(goldenGateBridgeReminder.latitude))
        assertThat(result.data.longitude, `is`(goldenGateBridgeReminder.longitude))
        assertThat(result.data.id, `is`(goldenGateBridgeReminder.id))
    }

    @Test
    fun saveRemindersAndGetReminders() = runTest {
        repository.saveReminder(goldenGateBridgeReminder)
        repository.saveReminder(mountEtnaReminder)
        repository.saveReminder(arcticReminder)

        val result = repository.getReminders()

        assertThat(result as Result.Success, notNullValue())
        assertThat(result.data.size, `is`(3))
    }


    @Test
    fun saveRemindersAndDeleteAllReminders() = runTest {
        repository.saveReminder(goldenGateBridgeReminder)
        repository.saveReminder(mountEtnaReminder)
        repository.saveReminder(arcticReminder)
        assertThat((repository.getReminders() as Result.Success).data.size, `is`(3))

        repository.deleteAllReminders()

        val result = repository.getReminders()

        assertThat(result as Result.Success, notNullValue())
        assertThat(result.data.size, `is`(0))
    }

    @Test
    fun getReminderAndReturnError() = runTest {
        val result = repository.getReminder(goldenGateBridgeReminder.id)

        assertThat(result as Result.Error, notNullValue())
        assertThat(result.message, `is`("Reminder not found!"))
    }
}