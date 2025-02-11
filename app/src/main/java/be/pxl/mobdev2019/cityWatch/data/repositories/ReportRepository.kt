package be.pxl.mobdev2019.cityWatch.data.repositories

import android.net.Uri
import be.pxl.mobdev2019.cityWatch.data.entities.Report
import be.pxl.mobdev2019.cityWatch.ui.list_report.Severity
import com.google.android.gms.maps.model.LatLng
import java.util.*

class ReportRepository(
    private val fireBase: FireBaseRepository
) {
    fun getReports() = fireBase.getReports()

    fun createReport(
        userId: String,
        title: String,
        description: String,
        severity: Severity,
        latLng: LatLng,
        creationDate: Long,
        reportImageUri: Uri,
        imageByteArray: ByteArray

    ) =
        fireBase.createReport(
            Report(
                userId = userId,
                title = title,
                description = description,
                severity = severity,
                latitude = latLng.latitude,
                longitude = latLng.longitude,
                creationDate = creationDate
            ),
            reportImageUri = reportImageUri,
            imageByteArray = imageByteArray
        )
}