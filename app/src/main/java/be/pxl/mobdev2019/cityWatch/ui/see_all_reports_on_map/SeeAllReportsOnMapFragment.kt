package be.pxl.mobdev2019.cityWatch.ui.see_all_reports_on_map


import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.util.toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback


class SeeAllReportsOnMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var seeAllReportsOnMapViewModel: SeeAllReportsOnMapViewModel
    private lateinit var googleMap: GoogleMap

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            googleMap = it
        }

        val permission = ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permission == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
            getCurrentLocation()
            addButtonsToMap()
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                555
            )
        }

    }

    private fun getCurrentLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val result =
            LocationServices.getSettingsClient(requireActivity())
                .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                val response: LocationSettingsResponse? =
                    task.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->  // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try { // Cast to a resolvable exception.
                            val resolvable =
                                exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                activity,
                                LocationRequest.PRIORITY_HIGH_ACCURACY
                            )
                        } catch (e: SendIntentException) { // Ignore the error.
                        } catch (e: ClassCastException) { // Ignore, should be an impossible error.
                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LocationRequest.PRIORITY_HIGH_ACCURACY -> when (resultCode) {
                Activity.RESULT_OK ->  // All required changes were successfully made
                    Log.i(TAG, "onActivityResult: GPS Enabled by user")
                Activity.RESULT_CANCELED ->  // The user was asked to change settings, but chose not to
                    Log.i(TAG, "onActivityResult: User rejected GPS request")
                else -> {
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {

        when (requestCode) {
            555 -> {
                if (grantResults.isEmpty() || grantResults[0] !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    toast("Unable to show current location - permission required")
                } else {
                    addButtonsToMap()
                }
            }
        }
    }

    private fun addButtonsToMap() {
        val mapSettings = googleMap.uiSettings
        mapSettings?.isMyLocationButtonEnabled = true
        mapSettings?.isCompassEnabled = true
        mapSettings?.isZoomControlsEnabled = true
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Change the map type based on the user's selection.
        R.id.normal_map -> {
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view: View = inflater.inflate(R.layout.fragment_see_all_posts_on_map, container, false)
        val mMapView = view.findViewById<MapView>(R.id.map_view)
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this)
        mMapView.onResume()
        return view
    }


}
