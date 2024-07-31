package com.udacity.project4

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository

object ServiceLocator {
    private val lock = Any()

    @Volatile
    var remindersLocalRepository: RemindersLocalRepository? = null
        @VisibleForTesting set

    fun provideRemindersLocalRepository(context: Context): RemindersLocalRepository {
        synchronized(this) {
            return remindersLocalRepository ?: createTasksRepository(context)
        }
    }

    private fun createTasksRepository(context: Context): RemindersLocalRepository {
        val newRepo = RemindersLocalRepository(LocalDB.createRemindersDao(context))
        remindersLocalRepository = newRepo
        return newRepo
    }

    @VisibleForTesting
    fun resetRepository() {
//        TODO: copied from android-testing course
//        synchronized(lock) {
//            runBlocking {
//                TasksRemoteDataSource.deleteAllTasks()
//            }
//            // Clear all data to avoid test pollution.
//            database?.apply {
//                clearAllTables()
//                close()
//            }
//            database = null
//            tasksRepository = null
//        }
    }
}