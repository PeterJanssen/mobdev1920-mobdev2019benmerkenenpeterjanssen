package be.pxl.mobdev2019.cityWatch.ui.see_all_reports_on_map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SeeAllReportsOnMapViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is see all posts Fragment"
    }
    val text: LiveData<String> = _text
}