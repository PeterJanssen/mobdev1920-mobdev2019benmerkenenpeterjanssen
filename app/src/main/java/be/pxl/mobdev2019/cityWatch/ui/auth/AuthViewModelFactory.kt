package be.pxl.mobdev2019.cityWatch.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import be.pxl.mobdev2019.cityWatch.data.repositories.FireBaseRepository

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(
    private val repository: FireBaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthViewModel(repository) as T
    }
}