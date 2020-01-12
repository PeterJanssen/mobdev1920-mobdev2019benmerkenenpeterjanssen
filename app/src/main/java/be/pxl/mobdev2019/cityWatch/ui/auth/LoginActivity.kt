package be.pxl.mobdev2019.cityWatch.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.ui.auth.fragments.ChooseLoginMethodFragment

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_login)
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

