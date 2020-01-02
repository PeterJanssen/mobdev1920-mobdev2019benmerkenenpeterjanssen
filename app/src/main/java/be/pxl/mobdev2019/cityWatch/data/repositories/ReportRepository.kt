package be.pxl.mobdev2019.cityWatch.data.repositories

import be.pxl.mobdev2019.cityWatch.data.entities.Report
import be.pxl.mobdev2019.cityWatch.ui.list_report.Severity

class ReportRepository(
    private val fireBase: FireBaseRepository
) {
    fun getReports() = fireBase.getReports()
    fun createReport(userId: String, title: String, description: String, severity: Severity) =
        fireBase.createReport(
            Report(userId = userId, title = title, description = description, severity = severity)
        )
}