package be.pxl.mobdev2019.cityWatch.ui.create_report


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.databinding.FragmentCreateReportBinding
import be.pxl.mobdev2019.cityWatch.ui.MainActivity
import be.pxl.mobdev2019.cityWatch.util.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class CreateReportFragment : Fragment(), CreateReportListener, KodeinAware {

    override val kodein by kodein()
    private lateinit var createReportViewModel: CreateReportViewModel
    private val factory: CreateReportViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentCreateReportBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_report, container, false)
        createReportViewModel =
            ViewModelProviders.of(this, factory).get(CreateReportViewModel::class.java)

        binding.viewmodel = createReportViewModel
        createReportViewModel.createReportListener = this
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onStarted() {
        toast("Creating Report")
    }

    override fun onSuccess() {
        toast("Report created!")
        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity?.startActivity(intent)
    }

    override fun onFailure(message: String) {
        toast(message)
    }
}
