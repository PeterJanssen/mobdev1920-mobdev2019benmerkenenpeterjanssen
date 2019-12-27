package be.pxl.mobdev2019.cityWatch.ui.account


import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import be.pxl.mobdev2019.cityWatch.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_change_display_name.*

class ChangeDisplayNameFragment : Fragment() {
    private var mDatabase: DatabaseReference? = null
    private var mCurrentUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_change_display_name, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        editDisplayNameButton.setOnClickListener {
            mCurrentUser = FirebaseAuth.getInstance().currentUser
            val userId = mCurrentUser!!.uid

            mDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

            val displayName = editDisplayNameEditText.text.toString().trim()

            if (!TextUtils.isEmpty(displayName)) {
                mDatabase!!.child("display_name").setValue(displayName)
                    .addOnCompleteListener { task: Task<Void> ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                activity,
                                "Display name updated successfully",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            Navigation.findNavController(it)
                                .navigate(R.id.action_navigation_display_name_to_navigation_account)
                        } else {
                            Toast.makeText(
                                activity,
                                "Display name not updated, something went wrong",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    activity,
                    "Please input a new display name",
                    Toast.LENGTH_LONG
                ).show()
            }


        }
    }
}