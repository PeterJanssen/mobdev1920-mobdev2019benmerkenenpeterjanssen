package be.pxl.mobdev2019.cityWatch.ui.see_all_reports_on_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import be.pxl.mobdev2019.cityWatch.data.repositories.ReportRepository

@Suppress("UNCHECKED_CAST")
class SeeAllReportsOnMapViewModelFactory(private val repository: ReportRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SeeAllReportsOnMapViewModel(repository) as T
    }
}