package be.pxl.mobdev2019.cityWatch.data.repositories

import android.net.Uri
import be.pxl.mobdev2019.cityWatch.data.entities.LoginUser
import be.pxl.mobdev2019.cityWatch.data.entities.RegisterUser
import io.reactivex.Completable

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

    fun changeDisplayName(displayName: String): Completable =
        fireBase.updateDisplayName(displayName = displayName)

    fun changeDisplayImage(
        displayImageUri: Uri,
        displayImageByteArray: ByteArray
    ): Completable =
        fireBase.updateDisplayImage(
            displayImageUri = displayImageUri,
            displayImageByteArray = displayImageByteArray
        )

    fun currentUser() = fireBase.currentUser()

    fun logout() = fireBase.logout()

    fun getAccountDisplay(userId: String) = fireBase.getDisplayAccount(userId = userId)
    fun addLikeToUser(userId: String, totalLikes: String) =
        fireBase.updateTotalLikesOfUser(userId = userId, totalLikes = totalLikes)
}