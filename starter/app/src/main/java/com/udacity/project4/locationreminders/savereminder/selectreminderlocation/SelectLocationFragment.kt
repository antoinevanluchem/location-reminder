package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.Locale

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    // Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val layoutId = R.layout.fragment_select_location
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setDisplayHomeAsUpEnabled(true)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        setUpMenu()

        binding.saveLocationButton.setOnClickListener {
            onLocationSelected()
        }

        return binding.root
    }

    //
    // Location permission
    //
    private var requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Timber.i(
                "ACCESS_FINE_LOCATION granted"
            )
            configureMapForAcceptedLocationPermission()
        } else {
            Timber.i(
                "ACCESS_FINE_LOCATION denied"
            )
            Snackbar.make(
                binding.selectLocationLayout, R.string.location_required_error, Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun requestLocationPermission() {
        Timber.i("Request location permission")
        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun isPermissionGranted(): Boolean {
        return checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun configureMapForAcceptedLocationPermission() {
        map.isMyLocationEnabled = true
        moveCamera()
    }

    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            configureMapForAcceptedLocationPermission()
        } else {
            requestLocationPermission()
        }
    }

    //
    // Save Button
    //
    private fun onLocationSelected() {
        currentMarker?.let {
            _viewModel.longitude.value = it.position.longitude
            _viewModel.latitude.value = it.position.latitude
            _viewModel.reminderSelectedLocationStr.value = it.title
        }

        val directions =
            SelectLocationFragmentDirections.actionSelectLocationFragmentToSaveReminderFragment()
        _viewModel.navigationCommand.value = NavigationCommand.To(directions)
    }

    //
    // Options menu
    //
    private fun setUpMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.map_options, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.normal_map -> {
                        map.mapType = GoogleMap.MAP_TYPE_NORMAL
                        true
                    }

                    R.id.hybrid_map -> {
                        map.mapType = GoogleMap.MAP_TYPE_HYBRID
                        true
                    }

                    R.id.satellite_map -> {
                        map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                        true
                    }

                    R.id.terrain_map -> {
                        map.mapType = GoogleMap.MAP_TYPE_TERRAIN
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    //
    // Map Configuration
    //
    private fun setMapStyle(googleMap: GoogleMap) {
        try {
            // Downloaded map from https://snazzymaps.com/style/72543/assassins-creed-iv
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.map_style
                )
            )
            if (!success) {
                Timber.e("Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Timber.e("Can't find style. Error: ", e)
        }
    }

    private fun moveCamera() {
        currentMarker?.let {
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        it.position.latitude, it.position.longitude
                    ), 15f
                )
            )
        } ?: fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            location.latitude, location.longitude
                        ), 15f
                    )
                )
            }
        }
    }

    //
    // Selecting location
    //
    private fun setLastMarker(googleMap: GoogleMap) {
        val longitude = _viewModel.longitude.value
        val latitude = _viewModel.latitude.value
        val title = _viewModel.reminderSelectedLocationStr.value

        if (longitude != null && latitude != null && title != null) {
            googleMap.clear()

            currentMarker = googleMap.addMarker(
                MarkerOptions().position(LatLng(latitude, longitude)).title(title)
            )
            currentMarker?.showInfoWindow()
        }
    }

    private fun setPoiClick(googleMap: GoogleMap) {
        googleMap.setOnPoiClickListener { poi ->
            googleMap.clear()

            currentMarker = googleMap.addMarker(
                MarkerOptions().position(poi.latLng).title(poi.name)
            )
            currentMarker?.showInfoWindow()
        }
    }

    private fun setMapLongClick(googleMap: GoogleMap) {
        googleMap.setOnMapLongClickListener { latLng ->
            val snippet = String.format(
                Locale.getDefault(), "Lat: %1$.5f, Long: %2$.5f", latLng.latitude, latLng.longitude
            )
            currentMarker = googleMap.addMarker(
                MarkerOptions().position(latLng).title(getString(R.string.dropped_pin))
                    .snippet(snippet)
            )

            currentMarker?.showInfoWindow()
        }
    }


    //
    // OnMapReadyCallback
    //
    override fun onMapReady(googleMap: GoogleMap) {
        Timber.i("OnMapReady called")
        map = googleMap

        setLastMarker(map)
        setMapStyle(map)
        setPoiClick(map)
        setMapLongClick(map)
        enableMyLocation()
    }
}