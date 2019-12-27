package be.pxl.mobdev2019.cityWatch.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is lists Fragment"
    }
    val text: LiveData<String> = _text
}