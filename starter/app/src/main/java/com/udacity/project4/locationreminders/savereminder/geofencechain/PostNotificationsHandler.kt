package com.udacity.project4.locationreminders.savereminder.geofencechain

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.udacity.project4.R
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import timber.log.Timber

class PostNotificationsHandler(fragment: SaveReminderFragment) : AbstractHandler(fragment) {

    override fun shouldHandle(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(
                fragment.requireContext(), Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            false
        }
    }

    override fun handle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            postNotificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            next?.execute()
        }
    }

    private val postNotificationsPermissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Timber.i("POST_NOTIFICATIONS granted!")
                next?.execute()
            } else {
                showPleaseAllowNotificationsToast()
            }
        }

    private fun showPleaseAllowNotificationsToast() {
        Toast.makeText(
            fragment.requireContext(),
            fragment.getString(R.string.please_allow_notifications),
            Toast.LENGTH_SHORT
        ).show()
    }
}