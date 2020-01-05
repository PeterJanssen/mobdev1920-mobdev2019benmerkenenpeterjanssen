package be.pxl.mobdev2019.cityWatch.ui.see_all_reports_on_map


import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.util.toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class SeeAllReportsOnMapFragment : Fragment(), OnMapReadyCallback, KodeinAware {
    private lateinit var googleMap: GoogleMap

    private lateinit var seeAllReportsOnMapViewModel: SeeAllReportsOnMapViewModel
    private val factory: SeeAllReportsOnMapViewModelFactory by instance()
    override val kodein by kodein()
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            googleMap = it
        }

        seeAllReportsOnMapViewModel.getReports()

        //googleMap.setOnMarkerClickListener(this)

        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permission == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
            mFusedLocationClient.lastLocation.addOnCompleteListener(activity!!) { locationClientTask ->
                val location: Location? = locationClientTask.result
                if (location != null) {
                    googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                location.latitude,
                                location.longitude
                            ), 12.0f
                        )
                    )
                }
            }
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

        seeAllReportsOnMapViewModel.reports.observe(viewLifecycleOwner, Observer { reports ->
            reports.forEach { report ->
                if (report.latitude != 0.0 && report.longitude != 0.0) {
                    googleMap.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                report.latitude,
                                report.longitude
                            )
                        ).title(report.title).snippet(report.description)
                    )
                }
            }
        })

    }

    /* override fun onMarkerClick(marker: Marker?): Boolean {
         googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker!!.position, 12.0f))
         return true
     }*/

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
        seeAllReportsOnMapViewModel =
            ViewModelProviders.of(this, factory).get(SeeAllReportsOnMapViewModel::class.java)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        val view: View = inflater.inflate(R.layout.fragment_see_all_posts_on_map, container, false)
        val mMapView = view.findViewById<MapView>(R.id.map_view)
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this)
        mMapView.onResume()
        return view
    }

    /*override fun onMarkerClick(marker: Marker?): Boolean {
        if (marker == null) {
            return false
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 16.0f))
            marker.showInfoWindow()

        }
        return true
    }*/


}
