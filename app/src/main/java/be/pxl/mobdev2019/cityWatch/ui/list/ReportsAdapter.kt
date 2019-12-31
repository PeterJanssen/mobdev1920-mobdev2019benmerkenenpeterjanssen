package be.pxl.mobdev2019.cityWatch.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.data.entities.Report
import be.pxl.mobdev2019.cityWatch.databinding.ItemPersonalReportsListBinding

class ReportsAdapter (
    private val reports: List<Report>,
    private val listener: RecyclerViewClickListener
) : RecyclerView.Adapter<ReportsAdapter.ReportViewHolder>(){

    override fun getItemCount() = reports.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ReportViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_personal_reports_list,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.recyclerviewMovieBinding.report = reports[position]
        /*holder.recyclerviewMovieBinding.buttonBook.setOnClickListener {
            listener.onRecyclerViewItemClick(holder.recyclerviewMovieBinding.buttonBook, reports[position])
        }
        holder.recyclerviewMovieBinding.layoutLike.setOnClickListener {
            listener.onRecyclerViewItemClick(holder.recyclerviewMovieBinding.layoutLike, reports[position])
        }*/
    }


    inner class ReportViewHolder(
        val recyclerviewMovieBinding: ItemPersonalReportsListBinding
    ) : RecyclerView.ViewHolder(recyclerviewMovieBinding.root)

}