package be.pxl.mobdev2019.cityWatch.report_activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.account_activities.AccountActivity
import be.pxl.mobdev2019.cityWatch.account_activities.MainActivity
import com.google.firebase.auth.FirebaseAuth

class ListOfReportsListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_reports_list)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_personal_reports_list, menu)


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        if(item.itemId == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        if(item.itemId == R.id.action_settings) {
            startActivity(Intent(this,
                AccountActivity::class.java))
        }

        return true
    }
}
