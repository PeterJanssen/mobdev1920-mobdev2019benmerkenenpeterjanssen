package be.pxl.mobdev2019.cityWatch.ui.list_report


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.data.entities.Report
import be.pxl.mobdev2019.cityWatch.data.repositories.FireBaseRepository
import kotlinx.android.synthetic.main.fragment_all_reports.*

class AllReportsFragment : Fragment(), RecyclerViewClickListener {

    private lateinit var allReportsViewModel: AllReportsViewModel
    private lateinit var factory: AllReportsViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_reports, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val repository = FireBaseRepository()
        factory = AllReportsViewModelFactory(repository)
        allReportsViewModel = ViewModelProviders.of(this, factory).get(AllReportsViewModel::class.java)

        allReportsViewModel.getReports()

        allReportsViewModel.reports.observe(viewLifecycleOwner, Observer { reports ->
            recycler_view.also {
                it.layoutManager = LinearLayoutManager(requireActivity())
                it.setHasFixedSize(true)
                it.adapter = ReportsAdapter(reports, this)
                it.adapter!!.notifyDataSetChanged()
            }
        })
    }

    override fun onRecyclerViewItemClick(view: View, report: Report) {

    }
}
