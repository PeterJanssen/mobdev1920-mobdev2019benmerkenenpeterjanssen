package be.pxl.mobdev2019.cityWatch.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.databinding.FragmentLoginBinding
import be.pxl.mobdev2019.cityWatch.ui.MainActivity
import be.pxl.mobdev2019.cityWatch.ui.auth.AuthListener
import be.pxl.mobdev2019.cityWatch.ui.auth.AuthViewModel
import be.pxl.mobdev2019.cityWatch.ui.auth.AuthViewModelFactory
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class LoginFragment : Fragment(), AuthListener, KodeinAware {

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
        Toast.makeText(activity, "Logging in", Toast.LENGTH_LONG).show()
    }

    override fun onSuccess() {
        Toast.makeText(activity, "Login succes!", Toast.LENGTH_LONG).show()
        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity?.startActivity(intent)
    }

    override fun onFailure(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }
}
