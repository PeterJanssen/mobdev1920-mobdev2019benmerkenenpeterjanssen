package be.pxl.mobdev2019.cityWatch.ui.see_all_reports_on_map


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.util.toast
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
        googleMap.isMyLocationEnabled = true
        mapSettings?.isCompassEnabled = true
        mapSettings?.isMyLocationButtonEnabled = true
        mapSettings?.isZoomControlsEnabled = true
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_see_all_posts_on_map, container, false)
        val mMapView = view.findViewById<MapView>(R.id.map_view)
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this)
        mMapView.onResume()
        return view
    }


}
