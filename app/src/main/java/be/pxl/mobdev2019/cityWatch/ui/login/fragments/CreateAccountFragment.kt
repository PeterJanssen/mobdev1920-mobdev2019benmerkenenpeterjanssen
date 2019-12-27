package be.pxl.mobdev2019.cityWatch.ui.login.fragments


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import be.pxl.mobdev2019.cityWatch.MainActivity
import be.pxl.mobdev2019.cityWatch.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_create_account.*

class CreateAccountFragment : Fragment() {
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_account, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        accountCreateButton.setOnClickListener {
            val email = accountEmailEditText.text.toString().trim()
            val password = accountPasswordText.text.toString().trim()
            val displayName = accountDisplayNameEditText.text.toString().trim()

            if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password) || !TextUtils.isEmpty(
                    displayName
                )
            ) {
                createAccount(email, password, displayName)
            } else {
                Toast.makeText(activity, "Please fill out the fields", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createAccount(email: String, password: String, displayName: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val currentUserId = mAuth!!.currentUser
                    val userId = currentUserId!!.uid

                    mDatabase =
                        FirebaseDatabase.getInstance().reference.child("Users").child(userId)

                    val userObject = HashMap<String, String>()
                    userObject["display_name"] = displayName
                    userObject["image"] = "default"
                    userObject["total_likes"] = "0"

                    mDatabase!!.setValue(userObject).addOnCompleteListener { task: Task<Void> ->
                        if (task.isSuccessful) {
                            if (task.isSuccessful) {
                                activity?.startActivity(Intent(activity, MainActivity::class.java))
                            } else {
                                Toast.makeText(activity, "Login Failed!", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(activity, "User Not Created!", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(activity, "User Not Created!", Toast.LENGTH_LONG).show()
                }
            }
    }
}
