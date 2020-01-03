package be.pxl.mobdev2019.cityWatch.util

import com.google.firebase.database.DataSnapshot

interface OnGetDataListener {
    fun onSuccess(dataSnapshot: DataSnapshot?)
    fun onStart()
    fun onFailure()
}