package com.udacity.project4.locationreminders.geofence

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class GeofenceTransitionsWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        TODO("Not yet implemented")
    }
}