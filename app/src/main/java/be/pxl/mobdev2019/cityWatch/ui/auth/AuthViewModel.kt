package be.pxl.mobdev2019.cityWatch.ui.auth

import androidx.lifecycle.ViewModel
import be.pxl.mobdev2019.cityWatch.data.repositories.UserRepository
import be.pxl.mobdev2019.cityWatch.util.ViewModelListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    var email: String? = null
    var password: String? = null
    var displayName: String? = null

    var authListener: ViewModelListener? = null

    private val disposables = CompositeDisposable()

    val user by lazy {
        userRepository.currentUser()
    }

    fun onLoginButtonClick() {
        authListener?.onStarted()
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            authListener?.onFailure("Invalid email or password")
            return
        }

        val disposable = userRepository.login(email!!, password!!).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //sending a success callback
                authListener?.onSuccess()
            }, {
                //sending a failure callback
                authListener?.onFailure("Failed to login please try again or check your internet connection")
            })
        disposables.add(disposable)
    }

    fun onRegisterButtonClick() {
        authListener?.onStarted()

        if (email.isNullOrEmpty()) {
            authListener?.onFailure("Email is required")
            return
        }

        if (password.isNullOrEmpty()) {
            authListener?.onFailure("Password is required")
            return
        }

        if (displayName.isNullOrEmpty()) {
            authListener?.onFailure("Display name is required")
            return
        }
        val disposable =
            userRepository.register(email!!, password!!, displayName!!).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //sending a success callback
                    authListener?.onSuccess()
                }, {
                    //sending a failure callback
                    authListener?.onFailure("Failed to register please try again or check your internet connection")
                })
        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}

