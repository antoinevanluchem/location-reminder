package com.udacity.project4.locationreminders.savereminder.geofencechain

import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment

class GeofenceChain(fragment: SaveReminderFragment) {

    private val foregroundPermissionHandler = ForegroundPermissionHandler(fragment)
    private val backgroundPermissionHandler = BackgroundPermissionHandler(fragment)
    private val locationSettingsHandler = LocationSettingsHandler(fragment)
    private val postNotificationsHandler = PostNotificationsHandler(fragment)
    private val addGeofenceHandler = AddGeofenceHandler(fragment)

    fun execute() {
        foregroundPermissionHandler.setNext(backgroundPermissionHandler)
            .setNext(locationSettingsHandler).setNext(postNotificationsHandler)
            .setNext(addGeofenceHandler)

        foregroundPermissionHandler.execute()
    }
}