package com.udacity.project4.locationreminders.savereminder.geofencechain

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment

abstract class AbstractHandler(
    protected val fragment: SaveReminderFragment,
) {
    protected var next: AbstractHandler? = null

    fun setNext(handler: AbstractHandler): AbstractHandler {
        next = handler

        return handler
    }

    fun execute() {
        if (shouldHandle()) {
            handle()
        } else {
            next?.execute()
        }
    }

    protected abstract fun shouldHandle(): Boolean
    protected abstract fun handle()

    protected fun showPermissionDeniedSnackbar(text: Int) {
        Snackbar.make(
            fragment.binding.saveReminderLayout, text, Snackbar.LENGTH_LONG
        ).setAction(R.string.settings) {
            // Displays App settings screen.
            fragment.startActivity(Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }.show()
    }

    protected fun isForegroundPermissionGranted(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            fragment.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    protected fun isBackgroundPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                fragment.requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}