package be.pxl.mobdev2019.cityWatch.ui.list_report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.data.entities.Report
import be.pxl.mobdev2019.cityWatch.databinding.ItemReportBinding
import kotlinx.android.synthetic.main.item_report.view.*

class ReportsAdapter(
    private val reports: List<Report>,
    private val listener: RecyclerViewClickListener
) : RecyclerView.Adapter<ReportsAdapter.ReportViewHolder>() {

    override fun getItemCount() = reports.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ReportViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_report,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.itemReportBinding.report = reports[position]

        val color = when (reports[position].severity) {
            Severity.VERY_LOW -> R.color.colorVeryLowSeverity
            Severity.LOW -> R.color.colorLowSeverity
            Severity.MEDIUM -> R.color.colorMediumSeverity
            Severity.HIGH -> R.color.colorHighSeverity
            Severity.VERY_HIGH -> R.color.colorVeryHighSeverity
        }

        holder.itemReportBinding.root.imageSeverity.setColorFilter(color)

        val action =
            AllReportsFragmentDirections.actionNavigationListsToNavigationReportDetail(reports[position])
        holder.itemReportBinding.root.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(action)
        }
        /*
        holder.itemReportBinding.buttonLike.setOnClickListener {
            listener.onRecyclerViewItemClick(holder.itemReportBinding.buttonLike, reports[position])
        }*/
    }


    inner class ReportViewHolder(
        val itemReportBinding: ItemReportBinding
    ) : RecyclerView.ViewHolder(itemReportBinding.root)

}