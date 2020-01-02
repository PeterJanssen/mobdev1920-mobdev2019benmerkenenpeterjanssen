package be.pxl.mobdev2019.cityWatch.data.repositories

import be.pxl.mobdev2019.cityWatch.data.entities.AccountDisplay
import be.pxl.mobdev2019.cityWatch.data.entities.LoginUser
import be.pxl.mobdev2019.cityWatch.data.entities.Report
import be.pxl.mobdev2019.cityWatch.data.entities.RegisterUser
import be.pxl.mobdev2019.cityWatch.ui.list_report.Severity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.core.Repo
import com.google.firebase.storage.FirebaseStorage
import durdinapps.rxfirebase2.DataSnapshotMapper
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Completable
import io.reactivex.internal.operators.flowable.FlowableFromIterable.subscribe
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

    fun login(loginUser: LoginUser): Completable = Completable.create { emitter ->
        fireBaseAuth.signInWithEmailAndPassword(loginUser.email, loginUser.password)
            .addOnCompleteListener {
                if (!emitter.isDisposed) {
                    if (it.isSuccessful)
                        emitter.onComplete()
                    else
                        emitter.onError(it.exception!!)
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
                        userObject["display_name"] = registerUser.displayName
                        userObject["image"] = registerUser.image
                        userObject["total_likes"] = registerUser.likes

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

    fun changeDisplayName(displayName: String): Completable = Completable.create { emitter ->
        val userId = currentUser()!!.uid

        fireBaseDatabase.child("Users").child(userId).child("display_name")
            .setValue(displayName.trim())
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

    fun changeDisplayImage(displayImage: String) = Completable.create { emitter ->

    }

    fun getReports(): List<Report> {
        val personalReports = listOf<Report>(
            Report("1", "test1", "test1", Severity.VERY_LOW),
            Report("2", "test2", "test2", Severity.LOW),
            Report("3", "test3", "test3", Severity.MEDIUM),
            Report("1", "test4", "test4", Severity.HIGH),
            Report("2", "test5", "test5", Severity.VERY_HIGH)
        )

        /*
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
            })*/
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

    fun getDisplayAccount(): AccountDisplay {
        return AccountDisplay("", "", "")
    }
}