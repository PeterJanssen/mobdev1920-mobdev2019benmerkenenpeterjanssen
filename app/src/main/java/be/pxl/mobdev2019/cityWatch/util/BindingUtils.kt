@file:JvmName("BindingUtils")

package be.pxl.mobdev2019.cityWatch.util

import androidx.databinding.InverseMethod
import be.pxl.mobdev2019.cityWatch.ui.list_report.Severity

@InverseMethod("positionToSeverity")
fun severityToPosition(severity: Severity?): Int {
    return severity?.ordinal ?: 0
}


fun positionToSeverity(position: Int): Severity {
    return Severity.values()[position]
}
