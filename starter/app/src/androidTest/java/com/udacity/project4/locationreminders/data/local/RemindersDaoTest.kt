package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
    private lateinit var database: RemindersDatabase

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


    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        database.reminderDao().saveReminder(goldenGateBridgeReminder)

        val loaded = database.reminderDao().getReminderById(goldenGateBridgeReminder.id)

        assertThat(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.title, `is`(goldenGateBridgeReminder.title))
        assertThat(loaded.description, `is`(goldenGateBridgeReminder.description))
        assertThat(loaded.location, `is`(goldenGateBridgeReminder.location))
        assertThat(loaded.longitude, `is`(goldenGateBridgeReminder.longitude))
        assertThat(loaded.latitude, `is`(goldenGateBridgeReminder.latitude))
        assertThat(loaded.id, `is`(goldenGateBridgeReminder.id))
    }
}