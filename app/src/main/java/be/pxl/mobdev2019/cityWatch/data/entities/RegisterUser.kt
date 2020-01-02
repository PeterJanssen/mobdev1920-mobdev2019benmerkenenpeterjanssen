package be.pxl.mobdev2019.cityWatch.data.entities

data class RegisterUser(
    var email: String,
    var password: String,
    var displayName: String,
    var image: String,
    var likes: String
)