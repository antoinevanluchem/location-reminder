package com.udacity.project4.locationreminders.savereminder

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity.RESULT_OK
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.Geofence.NEVER_EXPIRE
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.properties.Delegates

class SaveReminderFragment : BaseFragment() {

    // Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireActivity(), GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_MUTABLE)
    }
    private val geofencingClient: GeofencingClient by lazy {LocationServices.getGeofencingClient(requireContext())}
    private var reminderData: ReminderDataItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val layoutId = R.layout.fragment_save_reminder
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)

        setDisplayHomeAsUpEnabled(true)
        binding.viewModel = _viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            // Navigate to another fragment to get the user location
            val directions = SaveReminderFragmentDirections
                .actionSaveReminderFragmentToSelectLocationFragment()
            _viewModel.navigationCommand.value = NavigationCommand.To(directions)
        }

        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value

            reminderData = ReminderDataItem(title, description, location, latitude, longitude)

            val isValidReminderData = _viewModel.validateEnteredData(reminderData!!)

            if (!isValidReminderData) {
                return@setOnClickListener
            }

            checkPermissions()

            val isForegroundPermissionAccepted = checkForegroundPermission()
            val isBackgroundPermissionAccepted = checkBackgroundPermission()

            if (isForegroundPermissionAccepted && isBackgroundPermissionAccepted) {
                checkDeviceLocationSettingsAndAddGeofence()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }

    //
    // Permissions
    //
    private fun checkPermissions() {
        val isForegroundPermissionAccepted = checkForegroundPermission()
        val isBackgroundPermissionAccepted = checkBackgroundPermission()

        if (!isForegroundPermissionAccepted) {
            Timber.i("isForegroundPermissionAccepted == false")
            requestForegroundPermission()
        }
        // Use else if here such that we do not request multiple permissions at once
        else if (!isBackgroundPermissionAccepted) {
            Timber.i("isBackgroundPermissionAccepted == false")
            requestBackgroundPermission()
        }
    }

    private fun showPermissionDeniedSnackbar(text: Int) {
        Snackbar.make(
            binding.saveReminderLayout,
            text, Snackbar.LENGTH_LONG
        )
            .setAction(R.string.settings) {
                // Displays App settings screen.
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }.show()
    }

    //
    // Foreground Permissions
    //
    private val foregroundPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Timber.i(
                "foregroundPermissionLauncher granted"
            )
            checkPermissions()
        } else {
            Timber.i(
                "foregroundPermissionLauncher denied"
            )
            showPermissionDeniedSnackbar(R.string.location_permission_denied_explanation)
        }
    }

    private fun requestForegroundPermission() {
        foregroundPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun checkForegroundPermission(): Boolean {
        return PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
    }

    //
    // Background Permissions
    //
    @TargetApi(Build.VERSION_CODES.Q)
    private val backgroundPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            Timber.i(
                "backgroundPermissionLauncher granted"
            )
            checkPermissions()
        } else {
            Timber.i(
                "backgroundPermissionLauncher denied"
            )
            showPermissionDeniedSnackbar(R.string.background_location_denied_explanation)
        }
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun requestBackgroundPermission() {
        backgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun checkBackgroundPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    //
    // checkDeviceLocationSettingsAndStartGeofence
    //

    private val locationSettingsLauncher: ActivityResultLauncher<IntentSenderRequest> = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            Timber.i("locationSettingsLauncher RESULT_OK")
            requestNotificationPermission()
            if(notificationsAllowed) {
                addGeofence()
            }
        } else {
            // Endless loop until user decides to turn on location.
            checkDeviceLocationSettingsAndAddGeofence()
        }

    }

    private fun checkDeviceLocationSettingsAndAddGeofence() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_LOW_POWER, 10000).build()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    Timber.i("Launching locationSettings")
                    locationSettingsLauncher.launch(IntentSenderRequest.Builder(exception.resolution).build())
                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.d("Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                    binding.saveReminderLayout,
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndAddGeofence()
                }.show()
            }
        }

        locationSettingsResponseTask.addOnCompleteListener {
            if ( it.isSuccessful ) {
                Timber.i("locationSettingsResponseTask successful!")

                requestNotificationPermission()
                if(notificationsAllowed) {
                    addGeofence()
                }
            }
        }
    }

    //
    // Notifications
    //
    private var notificationsAllowed = false

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), RC_NOTIFICATION)
        } else {
            notificationsAllowed = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == RC_NOTIFICATION) {
            notificationsAllowed = if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addGeofence()
                true
            } else {
                showPleaseAllowNotificationsToast()
                false
            }
        }
    }

    private fun showPleaseAllowNotificationsToast() {
        Toast.makeText(requireContext(), getString(R.string.please_allow_notifications), Toast.LENGTH_SHORT)
            .show()
    }

    //
    // Geofence
    //
    private fun addGeofence() {
        if (reminderData == null) {
            Timber.i("reminderData is null, not adding Geofence")
            return
        }

        val geofence = Geofence.Builder()
            .setRequestId(reminderData!!.id)
            .setCircularRegion(reminderData!!.latitude!!,
                reminderData!!.longitude!!,
                GEOFENCE_RADIUS_IN_METERS
        )
            .setExpirationDuration(NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val isForegroundPermissionAccepted = checkForegroundPermission()
        val isBackgroundPermissionAccepted = checkBackgroundPermission()

        if (!isForegroundPermissionAccepted){
            Timber.i("Not adding geofence - foregroundPermission is not accepted")
            return
        }
        if (!isBackgroundPermissionAccepted) {
            Timber.i("Not adding geofence - backgroundPermission is not accepted")
            return
        }

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
            addOnSuccessListener {
                Timber.i("Successfully added geofence with id ${geofence.requestId}")
                _viewModel.validateAndSaveReminder(reminderData!!)
            }
            addOnFailureListener {
                _viewModel.showToast.value = requireContext().getString(R.string.error_adding_geofence)
            }
        }
    }
}

private const val GEOFENCE_RADIUS_IN_METERS = 100f
private const val RC_NOTIFICATION = 99

