package be.pxl.mobdev2019.cityWatch.util

interface ViewModelListener {
    fun onStarted()
    fun onSuccess()
    fun onFailure(message: String)
}