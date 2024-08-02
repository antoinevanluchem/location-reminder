package com.udacity.project4.locationreminders.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import timber.log.Timber

/**
 * Triggered by the Geofence.  Since we can have many Geofences at once, we pull the request
 * ID from the first Geofence, and locate it within the cached data in our Room DB
 *
 * Or users can add the reminders and then close the app, So our app has to run in the background
 * and handle the geofencing in the background.
 * To do that you can use https://developer.android.com/reference/android/support/v4/app/JobIntentService to do that.
 *
 */
class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofenceEvent = GeofencingEvent.fromIntent(intent)

        if (geofenceEvent == null) {
            Timber.v("Could not create geofenceEvent from intent.")
            return
        }

        if (geofenceEvent.hasError()) {
            Timber.e(geofenceEvent.errorCode.toString())
            return
        }

        if (geofenceEvent.geofenceTransition != Geofence.GEOFENCE_TRANSITION_ENTER) {
            Timber.v("We are not interested in this geofenceTransition, since it is not GEOFENCE_TRANSITION_ENTER")
            return
        }

        if (geofenceEvent.triggeringGeofences.isNullOrEmpty()) {
            Timber.e("triggeringGeofences is Null or Empty!")
            return
        }

        val workData = Data.Builder().apply {
            putStringArray(
                GEOFENCE_IDS_KEY,
                geofenceEvent.triggeringGeofences!!.map { it.requestId }.toTypedArray()
            )
        }.build()

        val geofenceWorkRequest = OneTimeWorkRequestBuilder<GeofenceTransitionsWorker>()
            .setInputData(workData)
            .build()

        WorkManager.getInstance(context).enqueue(geofenceWorkRequest)
    }

    companion object {
        private const val GEOFENCE_IDS_KEY = "GEOFENCE_IDS_KEY"
    }

}