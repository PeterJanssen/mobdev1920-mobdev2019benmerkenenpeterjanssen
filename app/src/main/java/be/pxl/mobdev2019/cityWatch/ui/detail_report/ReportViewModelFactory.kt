package be.pxl.mobdev2019.cityWatch.ui.detail_report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import be.pxl.mobdev2019.cityWatch.data.repositories.UserRepository

@Suppress("UNCHECKED_CAST")
class ReportViewModelFactory(private val repository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ReportViewModel(repository) as T
    }
}