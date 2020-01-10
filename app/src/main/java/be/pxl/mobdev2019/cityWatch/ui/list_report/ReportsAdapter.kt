package be.pxl.mobdev2019.cityWatch.ui.list_report

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.data.entities.Report
import be.pxl.mobdev2019.cityWatch.databinding.ItemReportBinding
import com.squareup.picasso.Picasso

class ReportsAdapter(
    private var reports: List<Report>
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
        reports = reports.sortedBy {
            it.creationDate
        }.reversed()
        holder.itemReportBinding.report = reports[position]

        if (reports[position].image.isNotEmpty()) {
            Picasso.get().load(Uri.parse(reports[position].image))
                .placeholder(R.drawable.ic_assignment_black_24dp)
                .into(holder.itemReportBinding.imageReport)
        }

        val action =
            AllReportsFragmentDirections.actionNavigationListsToNavigationReportDetail(reports[position])
        holder.itemReportBinding.root.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(action)
        }
    }


    inner class ReportViewHolder(
        val itemReportBinding: ItemReportBinding
    ) : RecyclerView.ViewHolder(itemReportBinding.root)

}