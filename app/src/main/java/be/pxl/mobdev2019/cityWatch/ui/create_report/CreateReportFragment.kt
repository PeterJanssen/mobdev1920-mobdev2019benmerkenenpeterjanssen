package be.pxl.mobdev2019.cityWatch.ui.create_report


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Build
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
import androidx.navigation.Navigation
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
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.fragment_create_report.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.io.ByteArrayOutputStream
import java.io.File


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateCreateReportButton()
    }

    private fun initiateCreateReportButton() {
        createReportButton.setOnClickListener {
            if (createReportViewModel.validateNewReport()) {
                checkAndroidVersionAndRequestPermissionsForCameraOrStorage()
            }
        }
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
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->  // Location settings_menu are not satisfied. But could be fixed by showing the
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
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

    private fun checkAndroidVersionAndRequestPermissionsForCameraOrStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ), 555
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            pickImage()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri = CropImage.getPickImageResultUri(this.context!!, data)
            cropRequest(imageUri)
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri

                val imageFile = File(resultUri.path!!)

                val imageBitmap =
                    Compressor(activity!!).setMaxWidth(300).setMaxHeight(300).setQuality(100)
                        .compressToBitmap(imageFile)

                val byteArray = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)

                val imageByteArray: ByteArray
                imageByteArray = byteArray.toByteArray()

                createReportViewModel.onCreateReportButtonClick(resultUri, imageByteArray)
            }

        }
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
        Navigation.findNavController(requireView())
            .navigate(R.id.action_navigation_create_report_to_navigation_home)
    }

    override fun onFailure(message: String) {
        toast(message)
    }
}
