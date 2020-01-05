package be.pxl.mobdev2019.cityWatch.ui.create_report


import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.databinding.FragmentCreateReportBinding
import be.pxl.mobdev2019.cityWatch.ui.MainActivity
import be.pxl.mobdev2019.cityWatch.util.ViewModelListener
import be.pxl.mobdev2019.cityWatch.util.toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class CreateReportFragment : Fragment(), ViewModelListener, KodeinAware {

    override val kodein by kodein()
    private lateinit var createReportViewModel: CreateReportViewModel
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val factory: CreateReportViewModelFactory by instance()

    private var latLng: LatLng = LatLng(0.0, 0.0)
    private val LOCATION_PERMISSION_ID = 42
    private val CAMERA_WRITE_READ_PERMISSION_ID = 555

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentCreateReportBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_report, container, false)
        createReportViewModel =
            ViewModelProviders.of(this, factory).get(CreateReportViewModel::class.java)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        binding.viewmodel = createReportViewModel
        createReportViewModel.createReportListener = this
        binding.lifecycleOwner = this

        if (checkPermissionsForCurrentLocation()) {
            getLastLocation()
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                LOCATION_PERMISSION_ID
            )
        }

        return binding.root
    }

    //Current Location handling
    private fun getLastLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val result =
            LocationServices.getSettingsClient(requireActivity())
                .checkLocationSettings(builder.build())

        result.addOnCompleteListener { resultTask ->
            try {
                val response: LocationSettingsResponse? =
                    resultTask.getResult(ApiException::class.java)

                mFusedLocationClient.lastLocation.addOnCompleteListener(activity!!) { locationClientTask ->
                    val location: Location? = locationClientTask.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        createReportViewModel.latLng = LatLng(location.latitude, location.longitude)
                        Log.d("CURRENT_LOCATION", latLng.toString())
                    }
                }
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
                        } catch (e: IntentSender.SendIntentException) { // Ignore the error.
                        } catch (e: ClassCastException) { // Ignore, should be an impossible error.
                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }

    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            createReportViewModel.latLng = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        }
    }

    //Permission handling
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
        if (requestCode == CAMERA_WRITE_READ_PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                pickImage()
            }
        }
    }

    private fun checkPermissionsForCurrentLocation(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    //Camera and local picture handling
    private fun pickImage() {
        CropImage.startPickImageActivity(this.context!!, this)
    }


    private fun cropRequest(imageUri: Uri) {
        CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMultiTouchEnabled(true)
            .setAspectRatio(1, 1)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this.context!!, this)
    }


    // handling report creation
    override fun onStarted() {
        toast("Creating Report")
    }

    override fun onSuccess() {
        toast("Report created!")
        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity?.startActivity(intent)
    }

    override fun onFailure(message: String) {
        toast(message)
    }
}
