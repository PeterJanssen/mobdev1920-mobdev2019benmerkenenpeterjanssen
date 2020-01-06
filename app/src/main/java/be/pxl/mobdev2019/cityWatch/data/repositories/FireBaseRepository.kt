package be.pxl.mobdev2019.cityWatch.data.repositories

import android.util.Log
import be.pxl.mobdev2019.cityWatch.data.entities.AccountDisplay
import be.pxl.mobdev2019.cityWatch.data.entities.LoginUser
import be.pxl.mobdev2019.cityWatch.data.entities.RegisterUser
import be.pxl.mobdev2019.cityWatch.data.entities.Report
import be.pxl.mobdev2019.cityWatch.util.OnGetDataListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
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

    private val DISPLAY_NAME_COLLUMN_NAME: String = "display_name"
    private val TOTAL_LIKES_COLLUMN_NAME: String = "total_likes"
    private val DISPLAY_IMAGE_COLLUMN_NAME: String = "image"


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
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userId = currentUser()!!.uid
                        val userObject = HashMap<String, String>()
                        userObject[DISPLAY_NAME_COLLUMN_NAME] = registerUser.displayName
                        userObject[TOTAL_LIKES_COLLUMN_NAME] = registerUser.likes
                        userObject[DISPLAY_IMAGE_COLLUMN_NAME] = registerUser.image

                        fireBaseDatabase.child(USER_DATABASE_TABLE).child(userId)
                            .setValue(userObject)
                            .addOnCompleteListener {
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

    fun changeDisplayName(displayName: String): Completable = Completable.create { emitter ->
        val userId = currentUser()!!.uid

        fireBaseDatabase.child(USER_DATABASE_TABLE).child(userId).child(DISPLAY_NAME_COLLUMN_NAME)
            .setValue(displayName.trim())
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

    fun changeDisplayImage(displayImage: String) = Completable.create { emitter ->

    }

    fun getReports(): List<Report> {
        /*val personalReports = listOf<Report>(
            Report("1", "test1", "test1", Severity.VERY_LOW),
            Report("2", "test2", "test2", Severity.LOW),
            Report("3", "test3", "test3", Severity.MEDIUM),
            Report("1", "test4", "test4", Severity.HIGH),
            Report("2", "test5", "test5", Severity.VERY_HIGH)
        )
*/
        val personalReports: MutableList<Report> = mutableListOf()
        val done = CountDownLatch(1)
        readData(fireBaseDatabase.child(REPORTS_DATABASE_TABLE), object : OnGetDataListener {
            override fun onSuccess(dataSnapshot: DataSnapshot?) {
                dataSnapshot!!.children.mapNotNullTo(personalReports) {
                    it.getValue<Report>(Report::class.java)
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

    fun createReport(report: Report): Completable =
        Completable.create { emitter ->
            fireBaseDatabase.child(REPORTS_DATABASE_TABLE).child(UUID.randomUUID().toString())
                .setValue(report)
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
                .addOnCompleteListener {
                    if (!emitter.isDisposed) {
                        if (it.isSuccessful)
                            emitter.onComplete()
                        else {
                            emitter.onError(it.exception!!)
                        }
                    }
                }
        }
}