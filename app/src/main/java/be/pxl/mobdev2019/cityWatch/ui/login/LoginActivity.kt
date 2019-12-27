package be.pxl.mobdev2019.cityWatch.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import be.pxl.mobdev2019.cityWatch.MainActivity
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.ui.login.fragments.ChooseLoginMethodFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { fireBaseAuth: FirebaseAuth ->
            user = fireBaseAuth.currentUser

            if (user != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else if (savedInstanceState == null) {
                setStarterFragment()
            }
        }
    }

    private fun setStarterFragment() {
        val chooseLoginMethodFragment =
            ChooseLoginMethodFragment()
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        transaction.replace(R.id.ActivityLoginFragmentContainer, chooseLoginMethodFragment)
        transaction.commit()
    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }
}

