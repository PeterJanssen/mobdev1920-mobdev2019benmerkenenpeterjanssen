package be.pxl.mobdev2019.cityWatch.account_activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import be.pxl.mobdev2019.cityWatch.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_account.*
import java.io.ByteArrayOutputStream
import java.io.File

class AccountActivity : AppCompatActivity() {

    private var mDataBase: DatabaseReference? = null
    private var mCurrentUser: FirebaseUser? = null
    private var mStorageRef: StorageReference? = null
    private var gallaryId: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        supportActionBar!!.title = "My Account"

        mCurrentUser = FirebaseAuth.getInstance().currentUser
        mStorageRef = FirebaseStorage.getInstance().reference

        val userId = mCurrentUser!!.uid

        mDataBase = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

        mDataBase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val displayName = dataSnapshot.child("display_name").value
                val image = dataSnapshot.child("image").value
                val userLikes = dataSnapshot.child("total_likes").value

                settingsDisplayName.text = displayName.toString()
                settingsLikesText.text = userLikes.toString()

                if (!image!!.equals("default")) {
                    Picasso.get().load(Uri.parse(image.toString()))
                        .placeholder(R.drawable.profile_img)
                        .into(settingsProfileImage)
                }

            }

            override fun onCancelled(databaseErrorSnapshot: DatabaseError) {

            }
        })

        settingsChangeDisplayNameButton.setOnClickListener {
            val intent = Intent(this, DisplayNameActivity::class.java)
            intent.putExtra("display_name", settingsDisplayName.text.toString().trim())
            startActivity(intent)
        }

        settingsChangeImageButton.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(galleryIntent, "SELECT_IMAGE"), gallaryId)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == gallaryId && resultCode == Activity.RESULT_OK) {
            val image: Uri = data!!.data!!

            CropImage.activity(image).setAspectRatio(1, 1).start(this)
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {
                var resultUri = result.uri

                val userId = mCurrentUser!!.uid
                val imageFile = File(resultUri.path!!)

                val imageBitmap = Compressor(this).setMaxWidth(200).setMaxHeight(200).setQuality(65)
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

                                        //save profile image

                                        mDataBase!!.updateChildren(updateObj)
                                            .addOnCompleteListener { task: Task<Void> ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(
                                                        this,
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
                super.onActivityResult(requestCode, resultCode, data)
            }

        }
    }
}
