package be.pxl.mobdev2019.cityWatch.ui.detail_report

import androidx.lifecycle.ViewModel
import be.pxl.mobdev2019.cityWatch.data.repositories.UserRepository
import be.pxl.mobdev2019.cityWatch.ui.list_report.Severity
import be.pxl.mobdev2019.cityWatch.util.ViewModelListener
import io.reactivex.disposables.CompositeDisposable

class ReportViewModel(private val repository: UserRepository) : ViewModel() {
    var title: String? = null
    var description: String? = null
    var severity: Severity = Severity.VERY_LOW

    var reportListener: ViewModelListener? = null

    private val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}