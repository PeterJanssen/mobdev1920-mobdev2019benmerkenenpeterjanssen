package be.pxl.mobdev2019.cityWatch.ui.list


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import be.pxl.mobdev2019.cityWatch.R

class ListsFragment : Fragment() {

    private lateinit var homeViewModel: ListsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(ListsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_lists, container, false)
        val textView: TextView = root.findViewById(R.id.text_lists)
        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}
