package com.udacity.project4.locationreminders.savereminder.geofencechain

import android.Manifest
import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.Geofence.NEVER_EXPIRE
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.udacity.project4.R
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import timber.log.Timber

class AddGeofenceHandler(fragment: SaveReminderFragment) :
    AbstractHandler(fragment) {

    override fun shouldHandle(): Boolean {
        if (fragment.reminderData == null) {
            Timber.i("reminderData is null, not adding Geofence")
            return false
        }

        if (!isForegroundPermissionGranted()) {
            Timber.i("Not adding geofence - foregroundPermission is not accepted")
            return false
        }
        if (!isBackgroundPermissionGranted()) {
            Timber.i("Not adding geofence - backgroundPermission is not accepted")
            return false
        }

        return true
    }

    override fun handle() {
        val geofence = Geofence.Builder().setRequestId(fragment.reminderData!!.id).setCircularRegion(
            fragment.reminderData!!.latitude!!, fragment.reminderData!!.longitude!!, GEOFENCE_RADIUS_IN_METERS
        ).setExpirationDuration(NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER).build()

        val geofencingRequest =
            GeofencingRequest.Builder().setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence).build()

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                Timber.i("Successfully added geofence with id ${geofence.requestId}")
                fragment._viewModel.validateAndSaveReminder(fragment.reminderData!!)
            }
            addOnFailureListener {
                fragment._viewModel.showToast.value =
                    fragment.requireContext().getString(R.string.error_adding_geofence)
            }
        }
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(fragment.requireActivity(), GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(fragment.requireContext(), 0, intent, PendingIntent.FLAG_MUTABLE)
    }
    private val geofencingClient: GeofencingClient by lazy {
        LocationServices.getGeofencingClient(
            fragment.requireContext()
        )
    }
}

private const val GEOFENCE_RADIUS_IN_METERS = 100f
