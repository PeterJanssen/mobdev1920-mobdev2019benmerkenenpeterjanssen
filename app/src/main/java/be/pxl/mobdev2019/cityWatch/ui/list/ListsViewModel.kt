package be.pxl.mobdev2019.cityWatch.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.pxl.mobdev2019.cityWatch.data.entities.Report
import be.pxl.mobdev2019.cityWatch.data.repositories.FireBaseRepository
import be.pxl.mobdev2019.cityWatch.util.Coroutines
import kotlinx.coroutines.Job

class ListsViewModel(private val repository: FireBaseRepository) : ViewModel() {
    private lateinit var job: Job

    private val _reports = MutableLiveData<List<Report>>()
    val reports: LiveData<List<Report>>
        get() = _reports

    fun getReports() {
        job = Coroutines.ioThenMain(
            { repository.getReports() },
            { _reports.value = it }
        )
    }

    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }
}