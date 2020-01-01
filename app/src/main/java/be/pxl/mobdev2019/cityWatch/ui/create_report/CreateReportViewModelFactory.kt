package be.pxl.mobdev2019.cityWatch.ui.create_report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import be.pxl.mobdev2019.cityWatch.data.repositories.ReportRepository

@Suppress("UNCHECKED_CAST")
class CreateReportViewModelFactory(private val repository: ReportRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreateReportViewModel(repository) as T
    }
}