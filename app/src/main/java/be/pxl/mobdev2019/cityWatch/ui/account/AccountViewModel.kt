package be.pxl.mobdev2019.cityWatch.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AccountViewModel : ViewModel() {

    private val _displayNameText = MutableLiveData<String>().apply {
        value = "DisplayName"
    }
    val displayNameText: LiveData<String> = _displayNameText

    private val _likesAmountText = MutableLiveData<String>().apply {
        value = "likes"
    }
    val likesAmountText: LiveData<String> = _likesAmountText

    private val _imageUri = MutableLiveData<String>().apply {
        value = "imageUri"
    }
    val ImageUri: LiveData<String> = _imageUri
}