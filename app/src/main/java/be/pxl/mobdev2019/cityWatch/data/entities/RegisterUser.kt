package be.pxl.mobdev2019.cityWatch.data.entities

data class RegisterUser(
    val email: String,
    val password: String,
    val displayName: String,
    val image: String,
    val likes: String
)