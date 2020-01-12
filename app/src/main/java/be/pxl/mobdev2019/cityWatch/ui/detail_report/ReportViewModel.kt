package be.pxl.mobdev2019.cityWatch.ui.detail_report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.pxl.mobdev2019.cityWatch.data.entities.AccountDisplay
import be.pxl.mobdev2019.cityWatch.data.entities.Report
import be.pxl.mobdev2019.cityWatch.data.repositories.UserRepository
import be.pxl.mobdev2019.cityWatch.ui.list_report.Severity
import be.pxl.mobdev2019.cityWatch.util.Coroutines
import be.pxl.mobdev2019.cityWatch.util.ViewModelListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Job

class ReportViewModel(private val repository: UserRepository) : ViewModel() {

    var report: Report? = null
    var reportListener: ViewModelListener? = null
    val accountDisplay: LiveData<AccountDisplay>
        get() = _accountDisplay

    private lateinit var job: Job
    private val _accountDisplay = MutableLiveData<AccountDisplay>()

    init {
        getAccountDisplayByUserId()
    }

    private fun getAccountDisplayByUserId() {
        job = Coroutines.ioThenMain(
            { repository.getAccountDisplay(report!!.userId) },
            { _accountDisplay.value = it }
        )
    }


    private val disposables = CompositeDisposable()

    fun onLikeFabButtonPress() {
        reportListener?.onStarted()
        val newTotalLikes: Int = _accountDisplay.value!!.likes.toInt() + 1
        val disposable =
            repository.addLikeToUser(report!!.userId, newTotalLikes.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //sending a success callback
                    reportListener?.onSuccess()
                }, {
                    //sending a failure callback
                    reportListener?.onFailure(it.message!!)
                })

        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}