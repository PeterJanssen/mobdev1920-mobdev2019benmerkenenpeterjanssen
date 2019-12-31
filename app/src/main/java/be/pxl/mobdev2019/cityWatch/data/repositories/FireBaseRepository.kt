package be.pxl.mobdev2019.cityWatch.data.repositories

import android.net.Uri
import be.pxl.mobdev2019.cityWatch.ui.list.Severity
import be.pxl.mobdev2019.cityWatch.data.entities.Report
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import io.reactivex.Completable

class FireBaseRepository {
    private val fireBaseDatabase: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference
    }

    private val fireBaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val Storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    fun login(email: String, password: String) = Completable.create { emitter ->
        fireBaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!emitter.isDisposed) {
                if (it.isSuccessful)
                    emitter.onComplete()
                else
                    emitter.onError(it.exception!!)
            }
        }
    }

    fun register(email: String, password: String, displayName: String) =
        Completable.create { emitter ->
            fireBaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val userId = currentUser()!!.uid
                    val userObject = HashMap<String, String>()
                    userObject["display_name"] = displayName
                    userObject["image"] = "default"
                    userObject["total_likes"] = "0"

                    fireBaseDatabase.child("Users").child(userId).setValue(userObject)
                        .addOnCompleteListener { task: Task<Void> ->
                            if (task.isSuccessful) {
                                if (!emitter.isDisposed) {
                                    if (it.isSuccessful)
                                        emitter.onComplete()
                                    else
                                        emitter.onError(it.exception!!)
                                }
                            }
                        }
                }
            }
        }

    fun logout() = fireBaseAuth.signOut()

    fun currentUser() = fireBaseAuth.currentUser

    fun changeDisplayName(displayName: String) = Completable.create { emitter ->
        val userId = currentUser()!!.uid
        fireBaseDatabase.child("Users").child(userId).child("display_name").setValue(displayName)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (!emitter.isDisposed) {
                        if (it.isSuccessful)
                            emitter.onComplete()
                        else
                            emitter.onError(it.exception!!)
                    }
                }
            }
    }

    fun changeImage(resultUri: Uri, imageByteArray: ByteArray) = Completable.create { emitter ->
        val imageFilePath =
            Storage.reference.child("citywatch_profile_images")
                .child("profile_image_${currentUser()!!.uid}")

        imageFilePath.putFile(resultUri)
            .addOnCompleteListener { taskSnapshot: Task<UploadTask.TaskSnapshot> ->
                if (taskSnapshot.isSuccessful) {
                    imageFilePath.downloadUrl.addOnSuccessListener { uri: Uri? ->
                        val uploadTask: UploadTask = imageFilePath.putBytes(imageByteArray)
                        uploadTask.addOnCompleteListener { task: Task<UploadTask.TaskSnapshot> ->
                            if (task.isSuccessful) {
                                val updateObj = HashMap<String, Any>()
                                updateObj["image"] = uri.toString()
                                fireBaseDatabase.updateChildren(updateObj)
                                    .addOnCompleteListener { task: Task<Void> ->
                                        if (task.isSuccessful) {
                                            emitter.onComplete()
                                        } else {
                                            emitter.onError(task.exception!!)
                                        }
                                    }
                            } else {
                                emitter.onError(task.exception!!)
                            }
                        }
                    }
                } else {
                    emitter.onError(taskSnapshot.exception!!)
                }
            }
    }

    fun getReports(): List<Report> {
        val personalReports = ArrayList<Report>()
        var report = Report(
            1,
            "Verkeerslicht kapot",
            "Verkeerslicht aan Plospa kapot",
            Severity.HIGH
        )
        personalReports.add(report)
        report = Report(
            2,
            "Put in de weg",
            "Weg naar UHasselt ligt vol putten",
            Severity.MEDIUM
        )
        personalReports.add(report)
        return personalReports
    }
}