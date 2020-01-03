package be.pxl.mobdev2019.cityWatch.data.entities

import android.os.Parcel
import android.os.Parcelable
import be.pxl.mobdev2019.cityWatch.ui.list_report.Severity
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Report(
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val severity: Severity = Severity.LOW
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        Severity.valueOf(parcel.readString()!!)
    )

    companion object : Parceler<Report> {
        override fun Report.write(parcel: Parcel, flags: Int) {
            parcel.writeString(userId)
            parcel.writeString(title)
            parcel.writeString(description)
            parcel.writeInt(severity.ordinal)
        }

        override fun create(parcel: Parcel): Report {
            return Report(parcel)
        }
    }
}