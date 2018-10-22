package com.swingfox.iseefire.iseefire.data

import com.google.gson.annotations.SerializedName

data class ReportFireRequest(val SecretUserId: String, val Latitude: Double, val Longitude: Double, val Text: String, val Distance: Double)

data class UpdateProfileRequest(val SecretUserId: String, val Name: String, val Phone: String)

open class BaseResponse {
    @SerializedName("error")
    val error: String? = null
}

class RegisterResponse : BaseResponse() {
    @SerializedName("secretUserId")
    val userId: String? = null
}

class ReportFireResponse : BaseResponse() {
    @SerializedName("reportId")
    val reportId: Int? = null
}

class Fire {
    @SerializedName("latitude")
    val latitude: Double = 0.0

    @SerializedName("longitude")
    val longitude: Double = 0.0

    @SerializedName("photoUrl")
    val photoUrl: String? = null

    @SerializedName("isOwner")
    val isOwner: Boolean = false

    @SerializedName("isNasa")
    val isNasa: Boolean = false

    @SerializedName("confidence")
    val confidence: Double = 0.0

    @SerializedName("distance")
    val distance: Double = 0.0

}

data class ImageRequest(val SecretUserId: String)

data class NearbyFiresRequest(
    val SecretUserId: String,
    val Latitude: Double,
    val Longitude: Double,
    val Distance: Double
)

class UpdateProfileResponse : BaseResponse()