package be.pxl.mobdev2019.cityWatch.ui.auth

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.ui.auth.fragments.ChooseLoginMethodFragment

class LoginActivity : AppCompatActivity() {
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (savedInstanceState == null) {
            setStarterFragment()
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
}

