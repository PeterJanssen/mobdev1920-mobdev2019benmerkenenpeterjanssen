package be.pxl.mobdev2019.cityWatch.data.repositories

import android.net.Uri
import be.pxl.mobdev2019.cityWatch.data.entities.Report
import be.pxl.mobdev2019.cityWatch.ui.list_report.Severity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import io.reactivex.Completable
import java.util.*
import kotlin.collections.ArrayList
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

    fun login(email: String, password: String): Completable = Completable.create { emitter ->
        fireBaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!emitter.isDisposed) {
                if (it.isSuccessful)
                    emitter.onComplete()
                else
                    emitter.onError(it.exception!!)
            }
        }
    }

    fun register(email: String, password: String, displayName: String): Completable =
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
            storage.reference.child("citywatch_profile_images")
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

        fireBaseDatabase.child("Reports")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    personalReports.clear()
                    for (postSnapshot in dataSnapshot.children) {
                        val userId = postSnapshot.child("userId").value
                        val description = postSnapshot.child("description").value
                        val severity = postSnapshot.child("severity").value
                        val title = postSnapshot.child("title").value
                        val report = Report(
                            userId.toString(),
                            title.toString(),
                            description.toString(),
                            Severity.valueOf(severity.toString())
                        )
                        personalReports.add(report)
                    }
                }

                override fun onCancelled(databaseErrorSnapshot: DatabaseError) {

                }
            })
        return personalReports
    }

    fun createReport(report: Report): Completable =
        Completable.create { emitter ->
            fireBaseDatabase.child("Reports").child(UUID.randomUUID().toString()).setValue(report)
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
}