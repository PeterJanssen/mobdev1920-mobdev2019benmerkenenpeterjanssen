package be.pxl.mobdev2019.cityWatch.ui

import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import androidx.preference.PreferenceManager
import be.pxl.mobdev2019.cityWatch.R

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var sharedPreferences: SharedPreferences? = null
    private var navView: BottomNavigationView? = null
    // Passing each menu ID as a set of Ids because each
    // menu should be considered as top level destinations.
    private val appBarConfiguration: AppBarConfiguration = AppBarConfiguration(
        setOf(
            R.id.navigation_home,
            R.id.navigation_create_report,
            R.id.navigation_see_all_posts,
            R.id.navigation_account
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView!!.setupWithNavController(navController)
        setUpShardedPreferences()
        setNavBarBackgroundColor()
        setToolBarBackgroundColor()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }

    private fun setNavBarBackgroundColor() {
        if (navView != null) {
            navView!!.setBackgroundColor(
                sharedPreferences!!.getInt(
                    getString(R.string.navBarBackgroundColorPickerPreference),
                    ContextCompat.getColor(baseContext, R.color.grey_100)
                )
            )
        }
    }

    private fun setToolBarBackgroundColor() {
        if (supportActionBar != null) {
            supportActionBar!!.setBackgroundDrawable(
                ColorDrawable(
                    sharedPreferences!!.getInt(getString(R.string.toolbarColorPickerPreference),
                        ContextCompat.getColor(baseContext, R.color.colorPrimary)
                    )
                )
            )
        }
    }

    private fun setUpShardedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key.equals(this.getString(R.string.clearPreferences))) {
            setToolBarBackgroundColor()
            setNavBarBackgroundColor()
        }
        if (key.equals(this.getString(R.string.toolbarColorPickerPreference))) {
            setToolBarBackgroundColor()
        }
        if (key.equals(this.getString(R.string.navBarBackgroundColorPickerPreference))) {
            setNavBarBackgroundColor()
        }
    }
}
