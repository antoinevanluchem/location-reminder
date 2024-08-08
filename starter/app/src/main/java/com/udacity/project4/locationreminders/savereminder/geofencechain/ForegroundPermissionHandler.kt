package com.udacity.project4.locationreminders.savereminder.geofencechain

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.udacity.project4.R
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import timber.log.Timber

class ForegroundPermissionHandler(fragment: SaveReminderFragment) :
    AbstractHandler(fragment) {

    override fun shouldHandle(): Boolean {
        return !isForegroundPermissionGranted()
    }

    override fun handle() {
        foregroundPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private val foregroundPermissionLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Timber.i(
                "foregroundPermissionLauncher granted"
            )
            next?.execute()
        } else {
            Timber.i(
                "foregroundPermissionLauncher denied"
            )
            showPermissionDeniedSnackbar(R.string.location_permission_denied_explanation)
        }
    }
}
