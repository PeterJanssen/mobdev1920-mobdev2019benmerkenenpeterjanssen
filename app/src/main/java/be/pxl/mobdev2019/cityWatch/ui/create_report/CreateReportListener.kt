package be.pxl.mobdev2019.cityWatch.ui.create_report

interface CreateReportListener {
    fun onStarted()
    fun onSuccess()
    fun onFailure(message: String)
}