package be.pxl.mobdev2019.cityWatch.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.databinding.FragmentLoginBinding
import be.pxl.mobdev2019.cityWatch.ui.MainActivity
import be.pxl.mobdev2019.cityWatch.ui.auth.AuthViewModel
import be.pxl.mobdev2019.cityWatch.ui.auth.AuthViewModelFactory
import be.pxl.mobdev2019.cityWatch.util.ViewModelListener
import be.pxl.mobdev2019.cityWatch.util.toast
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class LoginFragment : Fragment(), ViewModelListener, KodeinAware {

    override val kodein by kodein()

    private lateinit var viewModel: AuthViewModel
    private val factory: AuthViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
          val binding: FragmentLoginBinding =  DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
          viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)

          binding.viewmodel = viewModel
          viewModel.authListener = this
          binding.lifecycleOwner = this
          return binding.root
    }

    override fun onStarted() {
        toast("Logging in")
    }

    override fun onSuccess() {
        toast("Login succes!")
        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("EXIT", true)
        activity?.startActivity(intent)
        activity?.finish()
    }

    override fun onFailure(message: String) {
        toast(message)
    }
}
