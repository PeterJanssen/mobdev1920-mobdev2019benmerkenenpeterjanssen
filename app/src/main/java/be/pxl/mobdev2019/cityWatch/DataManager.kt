package be.pxl.mobdev2019.cityWatch

import be.pxl.mobdev2019.cityWatch.ui.list.Severity

object DataManager {
    val personalReports = ArrayList<Report>()

    init {
        initializePersonalReports()
    }

    private fun initializePersonalReports() {
        var report = Report("Verkeerslicht kapot", "Verkeerslicht aan Plospa kapot", Severity.HIGH)
        personalReports.add(report)
        report = Report("Put in de weg", "Weg naar UHasselt ligt vol putten", Severity.MEDIUM)
        personalReports.add(report)
    }
}