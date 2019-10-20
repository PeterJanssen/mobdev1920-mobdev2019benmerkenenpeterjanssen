package be.pxl.mobdev2019.cityWatch

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReportRecyclerAdapter(private val context: Context, private val personalReports: List<Report>):
    RecyclerView.Adapter<ReportRecyclerAdapter.ViewHolder>() {

    private val layoutInflayer = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflayer.inflate(R.layout.item_personal_reports_list, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount() = personalReports.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report = personalReports[position]
        holder.textTitle.text = report.title
        holder.textDescription.text = report.description

        val color = when (report.severity) {
            Severity.VERY_LOW -> R.color.colorVeryLowSeverity
            Severity.LOW -> R.color.colorLowSeverity
            Severity.MEDIUM -> R.color.colorMediumSeverity
            Severity.HIGH -> R.color.colorHighSeverity
            Severity.VERY_HIGH -> R.color.colorVeryHighSeverity
        }

        holder.imageSeverity.setColorFilter(color)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle = itemView.findViewById<TextView>(R.id.textReportTitle)
        val textDescription = itemView.findViewById<TextView>(R.id.textReportDescription)
        val imageSeverity = itemView.findViewById<ImageView>(R.id.imageSeverity)
    }
}

