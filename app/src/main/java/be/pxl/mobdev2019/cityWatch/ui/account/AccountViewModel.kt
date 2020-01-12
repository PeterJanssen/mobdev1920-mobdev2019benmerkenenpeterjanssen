package be.pxl.mobdev2019.cityWatch.ui.account

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.pxl.mobdev2019.cityWatch.data.entities.AccountDisplay
import be.pxl.mobdev2019.cityWatch.data.repositories.UserRepository
import be.pxl.mobdev2019.cityWatch.util.Coroutines
import be.pxl.mobdev2019.cityWatch.util.ViewModelListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Job

class AccountViewModel(private val userRepository: UserRepository) : ViewModel() {
    private lateinit var job: Job

    private val _accountDisplay = MutableLiveData<AccountDisplay>()
    val accountDisplay: LiveData<AccountDisplay>
        get() = _accountDisplay

    init {
        getDisplayAccount()
    }
    
    private fun getDisplayAccount() {
        job = Coroutines.ioThenMain(
            { userRepository.getAccountDisplay(userRepository.currentUser()!!.uid) },
            { _accountDisplay.value = it }
        )
    }

    var displayName: String? = null

    var accountListener: ViewModelListener? = null

    private val disposables = CompositeDisposable()

    fun onChangeDisplayNameButtonClick() {
        accountListener?.onStarted()

        if (displayName.isNullOrEmpty()) {
            accountListener?.onFailure("Invalid display name")
            return
        }
        val disposable =
            userRepository.changeDisplayName(displayName = displayName.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //sending a success callback
                    accountListener?.onSuccess()
                }, {
                    //sending a failure callback
                    accountListener?.onFailure("Could not change the display name please try again")
                })

        disposables.add(disposable)
    }

    fun onChangeDisplayImageButtonClick(displayImageUri: Uri, imageByteArray: ByteArray) {
        accountListener?.onStarted()
        val disposable = userRepository.changeDisplayImage(
            displayImageUri = displayImageUri,
            displayImageByteArray = imageByteArray
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //sending a success callback
                accountListener?.onSuccess()
            }, {
                //sending a failure callback
                accountListener?.onFailure("Could not change the image please try again")
            })

        disposables.add(disposable)
    }

    fun onLogoutButtonClick() {
        userRepository.logout()
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
        if (::job.isInitialized) job.cancel()
    }
}