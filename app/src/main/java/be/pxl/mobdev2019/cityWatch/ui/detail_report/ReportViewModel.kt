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
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Job

class ReportViewModel(private val repository: UserRepository) : ViewModel() {

    var report: Report? = null
    val accountDisplay: LiveData<AccountDisplay>
        get() = _accountDisplay

    private lateinit var job: Job
    private val _accountDisplay = MutableLiveData<AccountDisplay>()

    fun getAccountDisplayByUserId() {
        job = Coroutines.ioThenMain(
            { repository.getAccountDisplay(report!!.userId) },
            { _accountDisplay.value = it }
        )
    }

    var reportListener: ViewModelListener? = null

    private val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}