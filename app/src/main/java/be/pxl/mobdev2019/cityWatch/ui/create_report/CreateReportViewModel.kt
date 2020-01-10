package be.pxl.mobdev2019.cityWatch.ui.create_report

import android.net.Uri
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

    fun validateNewReport(): Boolean {
        if (title.isNullOrEmpty()) {
            createReportListener?.onFailure("Please input a title.")
            return false
        }

        if (description.isNullOrEmpty()) {
            createReportListener?.onFailure("Please input a description.")
            return false
        }

        if (latLng == LatLng(0.0, 0.0)) {
            createReportListener?.onFailure("You need to turn on your GPS to add a report, if this problem persists please refresh.")
            return false
        }
        return true
    }

    fun onCreateReportButtonClick(reportImageUri: Uri, imageByteArray: ByteArray) {
        title = title!!.trim().replace("\\s+".toRegex(), " ")
        description =
            description!!.trim().replace("\\s+".toRegex(), " ")

        createReportListener?.onStarted()
        val disposable =
            repository.createReport(
                id!!,
                title!!,
                description!!,
                severity,
                latLng,
                Date().time,
                reportImageUri,
                imageByteArray
            )
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