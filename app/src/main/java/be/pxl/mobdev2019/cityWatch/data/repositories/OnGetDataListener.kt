package be.pxl.mobdev2019.cityWatch.data.repositories

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

interface OnGetDataListener {
    fun onSuccess(dataSnapshot: DataSnapshot?)
    fun onStart()
    fun onFailure(databaseError: DatabaseError)
}