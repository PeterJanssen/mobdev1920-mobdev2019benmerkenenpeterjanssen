package be.pxl.mobdev2019.cityWatch.data.entities

import android.os.Parcel
import android.os.Parcelable
import be.pxl.mobdev2019.cityWatch.ui.list_report.Severity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.ServerValue
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Report(
    val userId: String = "",
    var title: String = "",
    var description: String = "",
    var severity: Severity = Severity.LOW,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val creationDate: Date = Date()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        Severity.valueOf(parcel.readString()!!),
        parcel.readDouble(),
        parcel.readDouble(),
        Date(parcel.readLong())
    )

    companion object : Parceler<Report> {
        override fun Report.write(parcel: Parcel, flags: Int) {
            parcel.writeString(userId)
            parcel.writeString(title)
            parcel.writeString(description)
            parcel.writeInt(severity.ordinal)
            parcel.writeDouble(latitude)
            parcel.writeDouble(longitude)
            parcel.writeLong(creationDate.time)
        }

        override fun create(parcel: Parcel): Report {
            return Report(parcel)
        }
    }
}