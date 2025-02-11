package be.pxl.mobdev2019.cityWatch.ui.list_report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import be.pxl.mobdev2019.cityWatch.data.repositories.ReportRepository

@Suppress("UNCHECKED_CAST")
class AllReportsViewModelFactory(
    private val repository: ReportRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AllReportsViewModel(repository) as T
    }
}