package be.pxl.mobdev2019.cityWatch.data.repositories

import be.pxl.mobdev2019.cityWatch.data.entities.Report
import be.pxl.mobdev2019.cityWatch.ui.list_report.Severity
import com.google.android.gms.maps.model.LatLng

class ReportRepository(
    private val fireBase: FireBaseRepository
) {
    fun getReports() = fireBase.getReports()
    fun createReport(
        userId: String,
        title: String,
        description: String,
        severity: Severity,
        latLng: LatLng
    ) =
        fireBase.createReport(
            Report(
                userId = userId,
                title = title,
                description = description,
                severity = severity,
                latitude = latLng.latitude,
                longitude = latLng.longitude
            )
        )
}