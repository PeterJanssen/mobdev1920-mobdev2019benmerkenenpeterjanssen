package be.pxl.mobdev2019.cityWatch.ui.auth

interface AuthListener {
    fun onStarted()
    fun onSuccess()
    fun onFailure(message: String)
}