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
import kotlinx.android.synthetic.main.fragment_all_reports.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class AllReportsFragment : Fragment(), RecyclerViewClickListener, KodeinAware {

    override val kodein by kodein()

    private lateinit var allReportsViewModel: AllReportsViewModel
    private val factory: AllReportsViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        allReportsViewModel =
            ViewModelProviders.of(this, factory).get(AllReportsViewModel::class.java)

        allReportsViewModel.getReports()

        allReportsViewModel.reports.observe(viewLifecycleOwner, Observer { reports ->
            recycler_view.also {
                it.layoutManager = LinearLayoutManager(requireActivity())
                it.setHasFixedSize(true)
                it.adapter = ReportsAdapter(reports, this)
            }
        })
        return inflater.inflate(R.layout.fragment_all_reports, container, false)
    }

    override fun onRecyclerViewItemClick(view: View, report: Report) {
        when (view.id) {

        }
    }
}
