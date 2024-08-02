package com.udacity.project4.locationreminders.geofence

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import timber.log.Timber

class GeofenceTransitionsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Timber.i("Doing some work!")
        return Result.success()
    }
}
