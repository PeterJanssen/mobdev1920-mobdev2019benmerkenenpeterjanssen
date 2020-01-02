package be.pxl.mobdev2019.cityWatch.data.repositories

import be.pxl.mobdev2019.cityWatch.data.entities.LoginUser
import be.pxl.mobdev2019.cityWatch.data.entities.RegisterUser

class UserRepository(
    private val fireBase: FireBaseRepository
) {
    fun login(email: String, password: String) =
        fireBase.login(LoginUser(email = email, password = password))

    fun register(email: String, password: String, displayName: String) =
        fireBase.register(
            RegisterUser(
                email = email,
                displayName = displayName,
                likes = "0",
                image = "default",
                password = password
            )
        )

    fun currentUser() = fireBase.currentUser()

    fun logout() = fireBase.logout()
}