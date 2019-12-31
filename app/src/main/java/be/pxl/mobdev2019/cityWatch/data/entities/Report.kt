package be.pxl.mobdev2019.cityWatch.data.entities
import be.pxl.mobdev2019.cityWatch.ui.list.Severity
data class Report(
    val id: Int,
    val title: String,
    val description: String,
    val severity: Severity
)