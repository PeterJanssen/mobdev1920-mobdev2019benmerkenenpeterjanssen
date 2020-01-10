package be.pxl.mobdev2019.cityWatch.data.repositories

import android.net.Uri
import android.util.Log
import be.pxl.mobdev2019.cityWatch.data.entities.AccountDisplay
import be.pxl.mobdev2019.cityWatch.data.entities.LoginUser
import be.pxl.mobdev2019.cityWatch.data.entities.RegisterUser
import be.pxl.mobdev2019.cityWatch.data.entities.Report
import be.pxl.mobdev2019.cityWatch.util.OnGetDataListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.reactivex.Completable
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.collections.HashMap


class FireBaseRepository {
    private val fireBaseDatabase: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference
    }

    private val fireBaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    private val USER_DATABASE_TABLE: String = "Users"
    private val REPORTS_DATABASE_TABLE: String = "Reports"
    private val REPORT_IMAGE_COLLUMN_NAME: String = "image"

    private val DISPLAY_NAME_COLLUMN_NAME: String = "display_name"
    private val TOTAL_LIKES_COLLUMN_NAME: String = "total_likes"
    private val DISPLAY_IMAGE_COLLUMN_NAME: String = "image"

    private val PROFILE_IMAGES_STORAGE_LOCATION = "citywatch_profile_images"
    private val PROFILE_IMAGE_ID = "profile_image_"

    private val REPORT_IMAGES_STORAGE_LOCATION = "citywatch_report_images"
    private val REPORT_IMAGE_ID = "report_image_"

    fun login(loginUser: LoginUser): Completable = Completable.create { emitter ->
        fireBaseAuth.signInWithEmailAndPassword(loginUser.email, loginUser.password)
            .addOnCompleteListener {
                if (!emitter.isDisposed) {
                    if (it.isSuccessful) {
                        emitter.onComplete()
                    } else {
                        emitter.onError(it.exception!!)
                    }
                }
            }
    }


    fun register(registerUser: RegisterUser): Completable =
        Completable.create { emitter ->
            fireBaseAuth.createUserWithEmailAndPassword(
                registerUser.email,
                registerUser.password
            )
                .addOnCompleteListener { createNewUser: Task<AuthResult> ->
                    if (createNewUser.isSuccessful) {
                        val userId = currentUser()!!.uid
                        val userObject = HashMap<String, String>()
                        userObject[DISPLAY_NAME_COLLUMN_NAME] = registerUser.displayName
                        userObject[TOTAL_LIKES_COLLUMN_NAME] = registerUser.likes
                        userObject[DISPLAY_IMAGE_COLLUMN_NAME] = registerUser.image

                        fireBaseDatabase.child(USER_DATABASE_TABLE).child(userId)
                            .setValue(userObject)
                            .addOnCompleteListener { saveNewUser ->
                                if (!emitter.isDisposed) {
                                    if (saveNewUser.isSuccessful)
                                        emitter.onComplete()
                                    else
                                        emitter.onError(saveNewUser.exception!!)
                                }
                            }
                    }
                }
        }

    fun logout() = fireBaseAuth.signOut()

    fun currentUser() = fireBaseAuth.currentUser

    fun changeDisplayName(displayName: String): Completable = Completable.create { emitter ->
        val userId = currentUser()!!.uid

        fireBaseDatabase.child(USER_DATABASE_TABLE).child(userId).child(DISPLAY_NAME_COLLUMN_NAME)
            .setValue(displayName.trim())
            .addOnCompleteListener { changeDisplayName ->
                if (!emitter.isDisposed) {
                    if (changeDisplayName.isSuccessful) {
                        emitter.onComplete()
                    } else {
                        emitter.onError(changeDisplayName.exception!!)
                    }
                }
            }
    }

    fun changeDisplayImage(displayImageUri: Uri, displayImageByteArray: ByteArray): Completable =
        Completable.create { emitter ->
            val imageFilePath =
                storage.reference.child(PROFILE_IMAGES_STORAGE_LOCATION)
                    .child("${PROFILE_IMAGE_ID}${currentUser()!!.uid}")

            imageFilePath.putFile(displayImageUri)
                .addOnCompleteListener { uploadImage: Task<UploadTask.TaskSnapshot> ->
                    if (uploadImage.isSuccessful) {
                        imageFilePath.downloadUrl.addOnSuccessListener { downloadedUri: Uri? ->
                            val uploadTask: UploadTask =
                                imageFilePath.putBytes(displayImageByteArray)
                            uploadTask.addOnCompleteListener { task: Task<UploadTask.TaskSnapshot> ->
                                if (task.isSuccessful) {
                                    val updateObj = HashMap<String, Any>()
                                    updateObj[DISPLAY_IMAGE_COLLUMN_NAME] = downloadedUri.toString()
                                    fireBaseDatabase.child(USER_DATABASE_TABLE)
                                        .child(currentUser()!!.uid)
                                        .updateChildren(updateObj)
                                        .addOnCompleteListener {
                                            if (!emitter.isDisposed) {
                                                if (it.isSuccessful) {
                                                    emitter.onComplete()
                                                } else {
                                                    emitter.onError(it.exception!!)
                                                }
                                            }
                                        }
                                }
                            }
                        }
                    }
                }
        }

    fun getReports(): List<Report> {
        val personalReports: MutableList<Report> = mutableListOf()
        val done = CountDownLatch(1)
        readData(fireBaseDatabase.child(REPORTS_DATABASE_TABLE), object : OnGetDataListener {
            override fun onSuccess(dataSnapshot: DataSnapshot?) {
                dataSnapshot!!.children.mapNotNullTo(personalReports) { report: DataSnapshot ->
                    report.getValue<Report>(Report::class.java)
                }
                done.countDown()
                Log.d("OnSucces", "Success")
            }

            override fun onStart() {
                Log.d("ONSTART", "Started")
            }

            override fun onFailure() {
                Log.d("onFailure", "Failed")
            }
        })
        done.await()
        return personalReports
    }

    private fun readData(ref: DatabaseReference, listener: OnGetDataListener) {
        listener.onStart()
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listener.onSuccess(dataSnapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                listener.onFailure()
            }
        })
    }

    fun createReport(
        report: Report,
        reportImageUri: Uri,
        imageByteArray: ByteArray
    ): Completable =
        Completable.create { emitter ->
            val key: String = UUID.randomUUID().toString()
            val imageFilePath: StorageReference =
                storage.reference.child(REPORT_IMAGES_STORAGE_LOCATION)
                    .child(currentUser()!!.uid)
                    .child("${REPORT_IMAGE_ID}${key}")

            fireBaseDatabase.child(REPORTS_DATABASE_TABLE).child(key)
                .setValue(report)
                .addOnCompleteListener { createReportTask ->
                    if (!emitter.isDisposed) {
                        if (createReportTask.isSuccessful) {
                            imageFilePath.putFile(reportImageUri)
                                .addOnCompleteListener { taskSnapshot: Task<UploadTask.TaskSnapshot> ->
                                    if (taskSnapshot.isSuccessful) {
                                        imageFilePath.downloadUrl.addOnSuccessListener { uri: Uri? ->
                                            val uploadTask: UploadTask =
                                                imageFilePath.putBytes(imageByteArray)
                                            uploadTask.addOnCompleteListener { task: Task<UploadTask.TaskSnapshot> ->
                                                if (task.isSuccessful) {
                                                    val imageObject = HashMap<String, Any>()
                                                    imageObject[REPORT_IMAGE_COLLUMN_NAME] =
                                                        uri.toString()
                                                    fireBaseDatabase.child(REPORTS_DATABASE_TABLE)
                                                        .child(key)
                                                        .updateChildren(imageObject)
                                                        .addOnCompleteListener { addImageToReportTask ->
                                                            if (!emitter.isDisposed) {
                                                                if (addImageToReportTask.isSuccessful) {
                                                                    emitter.onComplete()
                                                                } else {
                                                                    emitter.onError(
                                                                        addImageToReportTask.exception!!
                                                                    )
                                                                }
                                                            }
                                                        }
                                                }

                                            }
                                        }
                                    }
                                }
                            emitter.onComplete()
                        } else {
                            emitter.onError(createReportTask.exception!!)
                        }
                    }
                }
        }

    fun getDisplayAccount(userId: String): AccountDisplay {
        val accountDisplay = AccountDisplay()
        val done = CountDownLatch(1)
        readData(
            fireBaseDatabase.child(USER_DATABASE_TABLE).child(userId),
            object : OnGetDataListener {
                override fun onSuccess(dataSnapshot: DataSnapshot?) {
                    if (dataSnapshot != null) {
                        accountDisplay.displayName =
                            dataSnapshot.child(DISPLAY_NAME_COLLUMN_NAME).value.toString()
                        accountDisplay.likes =
                            dataSnapshot.child(TOTAL_LIKES_COLLUMN_NAME).value.toString()
                        accountDisplay.displayImage =
                            dataSnapshot.child(DISPLAY_IMAGE_COLLUMN_NAME).value.toString()
                    }
                    done.countDown()
                    Log.d("OnSucces", "Success")
                }

                override fun onStart() {
                    Log.d("ONSTART", "Started")
                }

                override fun onFailure() {
                    Log.d("onFailure", "Failed")
                }
            })
        done.await()
        return accountDisplay
    }

    fun addLikeToUser(userId: String, totalLikes: String): Completable =
        Completable.create { emitter ->
            fireBaseDatabase.child(USER_DATABASE_TABLE).child(userId)
                .child(TOTAL_LIKES_COLLUMN_NAME)
                .setValue(totalLikes)
                .addOnCompleteListener { addLikeToUser ->
                    if (!emitter.isDisposed) {
                        if (addLikeToUser.isSuccessful)
                            emitter.onComplete()
                        else {
                            emitter.onError(addLikeToUser.exception!!)
                        }
                    }
                }
        }
}