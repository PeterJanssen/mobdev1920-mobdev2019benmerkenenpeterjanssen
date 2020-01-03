package be.pxl.mobdev2019.cityWatch.ui.detail_report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.data.entities.Report
import be.pxl.mobdev2019.cityWatch.databinding.FragmentReportDetailBinding
import be.pxl.mobdev2019.cityWatch.util.ViewModelListener
import be.pxl.mobdev2019.cityWatch.util.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ReportFragment : Fragment(), ViewModelListener, KodeinAware {

    override val kodein by kodein()
    private lateinit var reportViewModel: ReportViewModel
    private val factory: ReportViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val report: Report = ReportFragmentArgs.fromBundle(arguments!!).report
        val binding: FragmentReportDetailBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_report_detail, container, false)
        reportViewModel =
            ViewModelProviders.of(this, factory).get(ReportViewModel::class.java)

        binding.viewmodel = reportViewModel
        reportViewModel.reportListener = this
        binding.lifecycleOwner = this

        reportViewModel.description = report.description
        reportViewModel.title = report.title
        reportViewModel.severity = report.severity

        return binding.root
    }

    override fun onStarted() {
        toast("Liking Report")
    }

    override fun onSuccess() {
        toast("Report liked!")
    }

    override fun onFailure(message: String) {
        toast(message)
    }

}
