package be.pxl.mobdev2019.cityWatch.ui.detail_report

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.data.entities.Report
import be.pxl.mobdev2019.cityWatch.databinding.FragmentReportDetailBinding
import be.pxl.mobdev2019.cityWatch.util.ViewModelListener
import be.pxl.mobdev2019.cityWatch.util.toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_report_detail.*
import kotlinx.android.synthetic.main.fragment_report_detail.accountProfileImage
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

        reportViewModel.report = report
        reportViewModel.getAccountDisplayByUserId()

        reportViewModel.accountDisplay.observe(viewLifecycleOwner, Observer { accountDisplay ->
            if (accountDisplay.displayImage != "default") {
                Picasso.get().load(Uri.parse(accountDisplay.displayImage))
                    .placeholder(R.drawable.profile_img)
                    .into(accountProfileImage)
            }
        })
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
