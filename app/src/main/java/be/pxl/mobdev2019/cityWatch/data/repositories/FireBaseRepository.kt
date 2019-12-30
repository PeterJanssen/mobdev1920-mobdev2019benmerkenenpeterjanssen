package be.pxl.mobdev2019.cityWatch.data.repositories

import android.net.Uri
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import io.reactivex.Completable
import java.io.File

class FireBaseRepository {
    private val fireBaseDatabase: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference.child("Users").child(currentUser()!!.uid)
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
                val userObject = HashMap<String, String>()
                userObject["display_name"] = displayName
                userObject["image"] = "default"
                userObject["total_likes"] = "0"
                fireBaseDatabase.child(currentUser()!!.uid).setValue(userObject)
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

    fun logout() = fireBaseAuth.signOut()

    fun currentUser() = fireBaseAuth.currentUser

    fun changeDisplayName(displayName: String) = Completable.create { emitter ->
        fireBaseDatabase.child("display_name").setValue(displayName)
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
}