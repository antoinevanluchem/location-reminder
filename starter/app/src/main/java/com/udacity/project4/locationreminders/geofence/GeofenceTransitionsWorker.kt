package com.udacity.project4.locationreminders.geofence

import android.app.NotificationManager
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.sendNotification
import org.koin.java.KoinJavaComponent.inject
import  com.udacity.project4.locationreminders.data.dto.Result.Success as Success
class GeofenceTransitionsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val geofenceIds = inputData.getStringArray(GEOFENCE_IDS_KEY) ?: return Result.failure()

        geofenceIds.forEach { requestId ->
            val remindersLocalRepository: ReminderDataSource by inject(ReminderDataSource::class.java)

            val result = remindersLocalRepository.getReminder(requestId)
            if (result is Success<ReminderDTO>) {
                val reminderDTO = result.data
                sendNotification(applicationContext, ReminderDataItem(
                    reminderDTO.title,
                    reminderDTO.description,
                    reminderDTO.location,
                    reminderDTO.latitude,
                    reminderDTO.longitude,
                    reminderDTO.id
                ))
            }
        }

        return Result.success()
    }

    companion object {
        private const val GEOFENCE_IDS_KEY = "GEOFENCE_IDS_KEY"
    }
}
