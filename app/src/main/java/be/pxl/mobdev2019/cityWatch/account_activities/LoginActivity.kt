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
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.loginButton

class LoginActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            val email = loginEmailEditText.text.toString().trim()
            val password = loginPasswordEditText.text.toString().trim()
            if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun loginUser(email: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val listOfPostListIntent = Intent(this, ListOfReportsListActivity::class.java)
                    startActivity(listOfPostListIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Login Failed!", Toast.LENGTH_LONG).show()
                }
            }
    }
}
