package be.pxl.mobdev2019.cityWatch.ui.see_all_posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SeeAllPostsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is see all posts Fragment"
    }
    val text: LiveData<String> = _text
}