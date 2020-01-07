package be.pxl.mobdev2019.cityWatch.ui.account


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.databinding.FragmentAccountBinding
import be.pxl.mobdev2019.cityWatch.ui.auth.LoginActivity
import be.pxl.mobdev2019.cityWatch.util.ViewModelListener
import be.pxl.mobdev2019.cityWatch.util.toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.fragment_account.*
import java.io.ByteArrayOutputStream
import java.io.File
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class AccountFragment : Fragment(), ViewModelListener, KodeinAware {

    override val kodein by kodein()

    private lateinit var accountViewModel: AccountViewModel
    private val factory: AccountViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAccountBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false)
        accountViewModel = ViewModelProviders.of(this, factory).get(AccountViewModel::class.java)

        binding.account = accountViewModel
        accountViewModel.accountListener = this
        binding.lifecycleOwner = this

        accountViewModel.getDisplayAccount()

        accountViewModel.accountDisplay.observe(viewLifecycleOwner, Observer { accountDisplay ->
            if (accountDisplay.displayImage != "default") {
                Picasso.get().load(Uri.parse(accountDisplay.displayImage))
                    .placeholder(R.drawable.profile_img)
                    .into(accountProfileImage)
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateLogoutButton()
        initiateChangePictureButton()
        initiateChangeDisplayNameButton()
    }

    private fun initiateLogoutButton() {
        accountLogoutButton.setOnClickListener {
            accountViewModel.onLogoutButtonClick()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("EXIT", true)
            activity?.startActivity(intent)
        }
    }

    private fun initiateChangeDisplayNameButton() {
        accountChangeDisplayNameButton.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_navigation_account_to_navigation_display_name)
        }
    }

    private fun initiateChangePictureButton() {
        accountChangeImageButton.setOnClickListener {
            checkAndroidVersionAndRequestPermissions()
        }
    }

    private fun checkAndroidVersionAndRequestPermissions() {
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

    private fun pickImage() {
        CropImage.startPickImageActivity(this.context!!, this)
    }

    private fun cropRequest(imageUri: Uri) {
        CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMultiTouchEnabled(true)
            .setAspectRatio(1, 1)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(this.context!!, this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImage()
        }
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
                    Compressor(activity!!).setMaxWidth(200).setMaxHeight(200).setQuality(65)
                        .compressToBitmap(imageFile)

                val byteArray = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)

                val imageByteArray: ByteArray
                imageByteArray = byteArray.toByteArray()

                accountViewModel.onChangeDisplayImageButtonClick(resultUri, imageByteArray)
            }

        }
    }

    override fun onStarted() {
        toast("Changing display image")
    }

    override fun onSuccess() {
        toast("Display image changed!")
    }

    override fun onFailure(message: String) {
        toast(message)
    }


}
