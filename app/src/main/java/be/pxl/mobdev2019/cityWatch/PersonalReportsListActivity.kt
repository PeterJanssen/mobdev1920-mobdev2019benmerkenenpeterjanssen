package be.pxl.mobdev2019.cityWatch

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager

import kotlinx.android.synthetic.main.activity_personal_reports_list.*
import kotlinx.android.synthetic.main.content_personal_reports_list.*
import kotlinx.android.synthetic.main.item_personal_reports_list.*
import kotlinx.android.synthetic.main.item_personal_reports_list.view.*

class PersonalReportsListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_reports_list)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Add a PersonalReportActivity", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        listPersonalReports.layoutManager = LinearLayoutManager(this)
        listPersonalReports.adapter = ReportRecyclerAdapter(this, DataManager.personalReports)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_personal_reports_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()

        listPersonalReports.adapter?.notifyDataSetChanged()
    }
}
