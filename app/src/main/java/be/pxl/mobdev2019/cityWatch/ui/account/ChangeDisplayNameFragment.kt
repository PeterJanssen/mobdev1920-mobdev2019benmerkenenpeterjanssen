package be.pxl.mobdev2019.cityWatch.ui.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.databinding.FragmentChangeDisplayNameBinding
import be.pxl.mobdev2019.cityWatch.util.ViewModelListener
import be.pxl.mobdev2019.cityWatch.util.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ChangeDisplayNameFragment : Fragment(), ViewModelListener, KodeinAware {
    override val kodein by kodein()

    private lateinit var viewModel: AccountViewModel
    private val factory: AccountViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentChangeDisplayNameBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_change_display_name, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(AccountViewModel::class.java)

        binding.viewmodel = viewModel
        viewModel.accountListener = this
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onStarted() {
        toast("Changing display name")
    }

    override fun onSuccess() {
        toast("Display name changed!")
        Navigation.findNavController(requireView())
            .navigate(R.id.action_navigation_display_name_to_navigation_account)
    }

    override fun onFailure(message: String) {
        toast(message)
    }
}