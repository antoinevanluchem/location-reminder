package com.udacity.project4.locationreminders.savereminder.geofencechain

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.udacity.project4.R
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import timber.log.Timber

class BackgroundPermissionHandler(fragment: SaveReminderFragment) :
    AbstractHandler(fragment) {

    override fun shouldHandle(): Boolean {
        return !isBackgroundPermissionGranted()
    }

    @TargetApi(Build.VERSION_CODES.Q)
    override fun handle() {
        backgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private val backgroundPermissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Timber.i(
                    "backgroundPermissionLauncher granted"
                )
                next?.execute()
            } else {
                Timber.i(
                    "backgroundPermissionLauncher denied"
                )
                showPermissionDeniedSnackbar(R.string.background_location_denied_explanation)
            }
        }
}