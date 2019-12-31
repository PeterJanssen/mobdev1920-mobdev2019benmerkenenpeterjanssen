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
import android.widget.Toast
import androidx.navigation.Navigation
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.ui.auth.LoginActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.fragment_account.*
import java.io.ByteArrayOutputStream
import java.io.File

class AccountFragment : Fragment() {

    private var mDataBase: DatabaseReference? = null
    private var mCurrentUser: FirebaseUser? = null
    private var mStorageRef: StorageReference? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mCurrentUser = FirebaseAuth.getInstance().currentUser
        mStorageRef = FirebaseStorage.getInstance().reference

        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initiateLogoutButton()
        initiateChangePictureButton()
        initiateChangeDisplayNameButton()

        val userId = mCurrentUser!!.uid

        mDataBase = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

        mDataBase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val displayName = dataSnapshot.child("display_name").value
                val image = dataSnapshot.child("image").value
                val userLikes = dataSnapshot.child("total_likes").value

                accountDisplayNameText.text = displayName.toString()
                accountLikesText.text =
                    activity?.applicationContext?.getString(
                        R.string.fragment_account_likes_text,
                        userLikes
                    )

                if (image == null || image != "default") {
                    Picasso.get().load(Uri.parse(image.toString()))
                        .placeholder(R.drawable.profile_img)
                        .into(accountProfileImage)
                }
            }

            override fun onCancelled(databaseErrorSnapshot: DatabaseError) {

            }
        })
    }

    private fun initiateLogoutButton() {
        accountLogoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
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
        } else {
            checkAndroidVersionAndRequestPermissions()
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

                val userId = mCurrentUser!!.uid
                val imageFile = File(resultUri.path!!)

                val imageBitmap =
                    Compressor(activity!!).setMaxWidth(200).setMaxHeight(200).setQuality(65)
                        .compressToBitmap(imageFile)

                val byteArray = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)

                val imageByteArray: ByteArray
                imageByteArray = byteArray.toByteArray()

                val imageFilePath =
                    mStorageRef!!.child("citywatch_profile_images").child("profile_image_$userId")

                imageFilePath.putFile(resultUri)
                    .addOnCompleteListener {
                            taskSnapshot: Task<UploadTask.TaskSnapshot> ->
                        if (taskSnapshot.isSuccessful) {
                            imageFilePath.downloadUrl.addOnSuccessListener { uri: Uri? ->
                                val uploadTask: UploadTask = imageFilePath.putBytes(imageByteArray)
                                uploadTask.addOnCompleteListener { task: Task<UploadTask.TaskSnapshot> ->
                                    if (task.isSuccessful) {
                                        val updateObj = HashMap<String, Any>()
                                        updateObj["image"] = uri.toString()
                                        mDataBase!!.updateChildren(updateObj)
                                            .addOnCompleteListener { task: Task<Void> ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(
                                                        activity,
                                                        "Profile image saved",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                    }
                                }
                            }
                        }
                    }
            }

        }
    }


}
