package com.udacity.project4.locationreminders.savereminder.geofencechain

import android.app.Activity.RESULT_OK
import android.content.IntentSender
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import timber.log.Timber

class LocationSettingsHandler(fragment: SaveReminderFragment) : AbstractHandler(fragment) {

    override fun shouldHandle(): Boolean {
        // Always handle LocationSettingsHandler, because checking if location services are enabled can only happen async in the handle function
        return true
    }

    override fun handle() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_LOW_POWER, 10000).build()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(fragment.requireActivity())
        val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    Timber.i("Launching locationSettings")
                    locationSettingsLauncher.launch(
                        IntentSenderRequest.Builder(exception.resolution).build()
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.d("Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                    fragment.binding.saveReminderLayout,
                    R.string.location_required_error,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    handle()
                }.show()
            }
        }

        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.i("locationSettingsResponseTask successful!")
                next?.execute()
            }
        }
    }

    private val locationSettingsLauncher: ActivityResultLauncher<IntentSenderRequest> =
        fragment.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Timber.i("locationSettingsLauncher RESULT_OK")
                next?.execute()
            } else {
                // Endless loop until user decides to turn on location.
                handle()
            }
        }
}