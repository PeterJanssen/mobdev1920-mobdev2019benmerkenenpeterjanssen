package be.pxl.mobdev2019.cityWatch.ui.create_report

import androidx.lifecycle.ViewModel
import be.pxl.mobdev2019.cityWatch.data.repositories.FireBaseRepository
import be.pxl.mobdev2019.cityWatch.data.repositories.ReportRepository
import be.pxl.mobdev2019.cityWatch.ui.list_report.Severity
import be.pxl.mobdev2019.cityWatch.util.ViewModelListener
import com.google.android.gms.maps.model.LatLng
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class CreateReportViewModel(private val repository: ReportRepository) : ViewModel() {

    var id: String? = FireBaseRepository().currentUser()!!.uid
    var title: String? = null
    var description: String? = null
    var severity: Severity = Severity.VERY_LOW
    var latLng: LatLng = LatLng(0.0, 0.0)

    var createReportListener: ViewModelListener? = null

    private val disposables = CompositeDisposable()

    fun onCreateReportButtonClick() {
        createReportListener?.onStarted()
        if (title.isNullOrEmpty()) {
            createReportListener?.onFailure("Invalid title")
            return
        }

        if (description.isNullOrEmpty()) {
            createReportListener?.onFailure("Invalid description")
            return
        }

        if (latLng == LatLng(0.0, 0.0)) {
            createReportListener?.onFailure("You need to turn on your GPS to add a report")
            return
        }

        val disposable =
            repository.createReport(id!!, title!!, description!!, severity, latLng, Date())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //sending a success callback
                    createReportListener?.onSuccess()
                }, {
                    //sending a failure callback
                    createReportListener?.onFailure(it.message!!)
                })
        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}