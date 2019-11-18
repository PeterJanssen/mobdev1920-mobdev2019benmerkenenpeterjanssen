package be.pxl.mobdev2019.cityWatch.account_activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import be.pxl.mobdev2019.cityWatch.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_display_name.*

class DisplayNameActivity : AppCompatActivity() {

    private var mDatabase: DatabaseReference? = null
    private var mCurrentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_display_name)

        supportActionBar!!.title = "Display Name"

        if (intent.extras!!.get("display_name") != null) {
            val oldDisplayName = intent.extras!!.get("display_name")
            editDisplayNameEditText.setText(oldDisplayName.toString())
        } else {
            editDisplayNameEditText.setText(R.string.activity_edit_display_name_edit_display_name_edit_text)
        }

        editDisplayNameButton.setOnClickListener {
            mCurrentUser = FirebaseAuth.getInstance().currentUser
            val userId = mCurrentUser!!.uid

            mDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

            val displayName = editDisplayNameEditText.text.toString().trim()

            mDatabase!!.child("display_name").setValue(displayName).addOnCompleteListener { task: Task<Void> ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Display name updated successfully", Toast.LENGTH_LONG)
                        .show()
                    startActivity(Intent(this, AccountActivity::class.java))
                } else {
                    Toast.makeText(
                        this,
                        "Display name not updated, something went wrong",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
