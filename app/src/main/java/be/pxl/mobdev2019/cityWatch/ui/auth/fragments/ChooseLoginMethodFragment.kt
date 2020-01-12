package be.pxl.mobdev2019.cityWatch.ui.auth.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import be.pxl.mobdev2019.cityWatch.R
import be.pxl.mobdev2019.cityWatch.databinding.FragmentChooseLoginMethodBinding
import be.pxl.mobdev2019.cityWatch.ui.MainActivity
import be.pxl.mobdev2019.cityWatch.ui.auth.AuthViewModel
import be.pxl.mobdev2019.cityWatch.ui.auth.AuthViewModelFactory
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import kotlinx.android.synthetic.main.fragment_choose_login_method.*

class ChooseLoginMethodFragment : Fragment(), KodeinAware {

    override val kodein by kodein()

    private lateinit var viewModel: AuthViewModel
    private val factory: AuthViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentChooseLoginMethodBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_choose_login_method,
            container,
            false
        )
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        viewModel.user?.let {
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity?.startActivity(intent)
            activity?.finish()
        }
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val fragmentTransaction: FragmentTransaction? = parentFragmentManager.beginTransaction()

        loginButton.setOnClickListener {
            val loginFragment =
                LoginFragment()
            fragmentTransaction?.replace(R.id.ActivityLoginFragmentContainer, loginFragment)
            fragmentTransaction?.addToBackStack(loginFragment.toString())
            fragmentTransaction?.commit()
        }

        createAccountButton.setOnClickListener {
            val createAccountFragment =
                CreateAccountFragment()
            fragmentTransaction?.replace(R.id.ActivityLoginFragmentContainer, createAccountFragment)
            fragmentTransaction?.addToBackStack(createAccountFragment.toString())
            fragmentTransaction?.commit()
        }
    }
}
