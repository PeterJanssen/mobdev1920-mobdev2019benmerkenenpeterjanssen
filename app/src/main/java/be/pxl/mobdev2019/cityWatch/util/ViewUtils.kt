package be.pxl.mobdev2019.cityWatch.util

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.toast(message: String){
    Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
}