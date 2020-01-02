package be.pxl.mobdev2019.cityWatch.ui.detail_report


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import be.pxl.mobdev2019.cityWatch.R

/**
 * A simple [Fragment] subclass.
 */
class ReportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_report_detail, container, false)
    }


}
