package be.pxl.mobdev2019.cityWatch.util

import java.io.IOException

class ApiException(message: String) : IOException(message)
class NoInternetException(message: String) : IOException(message)