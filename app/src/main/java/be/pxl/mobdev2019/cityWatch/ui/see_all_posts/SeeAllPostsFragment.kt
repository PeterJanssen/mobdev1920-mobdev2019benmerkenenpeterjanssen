package be.pxl.mobdev2019.cityWatch.ui.see_all_posts


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import be.pxl.mobdev2019.cityWatch.R

class SeeAllPostsFragment : Fragment() {

    private lateinit var notificationsViewModel: SeeAllPostsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProviders.of(this).get(SeeAllPostsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_see_all_posts, container, false)
        val textView: TextView = root.findViewById(R.id.text_see_all_posts)
        notificationsViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}
