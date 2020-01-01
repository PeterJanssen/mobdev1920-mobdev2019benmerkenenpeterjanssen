package be.pxl.mobdev2019.cityWatch.data.entities
import be.pxl.mobdev2019.cityWatch.ui.list_report.Severity
data class Report(
    val userId: String,
    val title: String,
    val description: String,
    val severity: Severity
)