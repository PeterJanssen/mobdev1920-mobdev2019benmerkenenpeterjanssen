package be.pxl.mobdev2019.cityWatch.ui.list


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
import kotlinx.android.synthetic.main.fragment_lists.*

class ListsFragment : Fragment(), RecyclerViewClickListener {

    private lateinit var listsViewModel: ListsViewModel
    private lateinit var factory: ListViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lists, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val repository = FireBaseRepository()
        factory = ListViewModelFactory(repository)
        listsViewModel = ViewModelProviders.of(this, factory).get(ListsViewModel::class.java)

        listsViewModel.getReports()

        listsViewModel.reports.observe(viewLifecycleOwner, Observer { reports ->
            recycler_view.also {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.setHasFixedSize(true)
                it.adapter = ReportsAdapter(reports, this)
            }
        })
    }

    override fun onRecyclerViewItemClick(view: View, report: Report) {

    }
}
