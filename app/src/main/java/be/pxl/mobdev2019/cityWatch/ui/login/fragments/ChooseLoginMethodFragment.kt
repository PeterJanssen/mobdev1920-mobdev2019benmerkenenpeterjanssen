package be.pxl.mobdev2019.cityWatch.ui.login.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import be.pxl.mobdev2019.cityWatch.R
import kotlinx.android.synthetic.main.fragment_choose_login_method.*

class ChooseLoginMethodFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_login_method, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val fragmentManager = fragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()

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
