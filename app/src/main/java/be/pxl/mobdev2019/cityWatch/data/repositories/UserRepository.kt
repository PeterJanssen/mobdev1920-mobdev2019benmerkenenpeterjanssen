package be.pxl.mobdev2019.cityWatch.data.repositories

class UserRepository(
    private val firebase: FireBaseRepository
) {
    fun login(email: String, password: String) = firebase.login(email, password)

    fun register(email: String, password: String, displayName: String) =
        firebase.register(email, password, displayName)

    fun currentUser() = firebase.currentUser()

    fun logout() = firebase.logout()
}