package be.pxl.mobdev2019.cityWatch.ui.see_all_reports_on_map


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import be.pxl.mobdev2019.cityWatch.R
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
