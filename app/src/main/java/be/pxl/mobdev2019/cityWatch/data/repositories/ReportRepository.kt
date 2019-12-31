package be.pxl.mobdev2019.cityWatch.data.repositories

class ReportRepository(
    private val firebase: FireBaseRepository
) {
    fun getReports() = firebase.getReports()
}