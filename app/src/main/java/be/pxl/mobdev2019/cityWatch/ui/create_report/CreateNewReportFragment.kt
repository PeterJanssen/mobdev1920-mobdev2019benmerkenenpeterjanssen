package be.pxl.mobdev2019.cityWatch.ui.create_report


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import be.pxl.mobdev2019.cityWatch.R

class CreateNewReportFragment : Fragment() {

    private lateinit var createNewReportViewModel: CreateNewReportViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        createNewReportViewModel =
            ViewModelProviders.of(this).get(CreateNewReportViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_create_new_report, container, false)
        val textView: TextView = root.findViewById(R.id.text_explore)
        createNewReportViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}
