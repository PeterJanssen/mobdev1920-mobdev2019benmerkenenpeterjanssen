package be.pxl.mobdev2019.cityWatch.account_activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.report_activities.ListOfReportsListActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_account.*

class CreateAccountActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

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
                Toast.makeText(this, "Please fill out the fields", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun createAccount(email: String, password: String, displayName: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val currentUserId = mAuth!!.currentUser
                    val userId = currentUserId!!.uid

                    mDatabase =
                        FirebaseDatabase.getInstance().reference.child("Users").child(userId)

                    val userObject = HashMap<String, String>()
                    userObject["display_name"] = displayName

                    mDatabase!!.setValue(userObject).addOnCompleteListener { task: Task<Void> ->
                        if (task.isSuccessful) {
                            if (task.isSuccessful) {
                                val listOfPostListIntent =
                                    Intent(this, ListOfReportsListActivity::class.java)
                                startActivity(listOfPostListIntent)
                                finish()
                            } else {
                                Toast.makeText(this, "Login Failed!", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(this, "User Not Created!", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "User Not Created!", Toast.LENGTH_LONG).show()
                }
            }
    }
}
