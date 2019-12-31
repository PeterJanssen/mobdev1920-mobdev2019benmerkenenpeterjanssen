package be.pxl.mobdev2019.cityWatch.ui.list

import android.view.View
import be.pxl.mobdev2019.cityWatch.data.entities.Report

interface RecyclerViewClickListener {
    fun onRecyclerViewItemClick(view: View, report: Report)
}