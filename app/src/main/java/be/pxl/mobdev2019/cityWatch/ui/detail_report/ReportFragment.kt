package be.pxl.mobdev2019.cityWatch.ui.detail_report

import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.data.entities.Report
import be.pxl.mobdev2019.cityWatch.databinding.FragmentReportDetailBinding
import be.pxl.mobdev2019.cityWatch.util.ViewModelListener
import be.pxl.mobdev2019.cityWatch.util.toast
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_report_detail.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class ReportFragment : Fragment(), ViewModelListener, KodeinAware {

    override val kodein by kodein()
    private lateinit var reportViewModel: ReportViewModel
    private val factory: ReportViewModelFactory by instance()
    private var sharedPreferences: SharedPreferences? = null

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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        changeColorOfCollapsingToolbar()
    }

    private fun changeColorOfCollapsingToolbar() {
        toolbar_layout.setContentScrimColor(
            sharedPreferences!!.getInt(
                getString(R.string.toolbarColorPickerPreference),
                ContextCompat.getColor(requireActivity().baseContext, R.color.colorPrimary)
            )
        )
        toolbar_layout.setStatusBarScrimColor(
            sharedPreferences!!.getInt(
                getString(R.string.toolbarColorPickerPreference),
                ContextCompat.getColor(requireActivity().baseContext, R.color.colorPrimary)
            )
        )
        toolbar_layout.setBackgroundColor(
            sharedPreferences!!.getInt(
                getString(R.string.toolbarColorPickerPreference),
                ContextCompat.getColor(requireActivity().baseContext, R.color.colorPrimary)
            )
        )
    }

    private fun addOnClickListenerToMapFab() {
        val fab: FloatingActionButton = view!!.findViewById(R.id.map_fab) as FloatingActionButton
        fab.setOnClickListener { reportDetailView ->
            val action =
                ReportFragmentDirections.actionNavigationReportDetailToNavigationSeeAllPosts()
                    .setLatLng(
                        LatLng(
                            reportViewModel.report!!.latitude,
                            reportViewModel.report!!.longitude
                        )
                    )
            Navigation.findNavController(reportDetailView)
                .navigate(action)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (reportViewModel.report!!.image.isNotEmpty()) {
            Picasso.get().load(Uri.parse(reportViewModel.report!!.image))
                .placeholder(R.drawable.ic_assignment_black_24dp)
                .into(report_image)
        }
        addOnClickListenerToMapFab()
    }

    override fun onStarted() {
    }

    override fun onSuccess() {
        val fab: FloatingActionButton = view!!.findViewById(R.id.like_fab) as FloatingActionButton
        fab.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                android.R.drawable.btn_star_big_on
            )
        )
        toast("Report liked!")
    }

    override fun onFailure(message: String) {
        toast(message)
    }

}
