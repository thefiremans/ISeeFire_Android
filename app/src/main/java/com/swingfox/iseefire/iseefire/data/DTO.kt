package com.swingfox.iseefire.iseefire.data

import com.google.gson.annotations.SerializedName

data class ReportFireRequest(val SecretUserId: String)

data class UpdateProfileRequest(val SecretUserId: String, val Name: String, val Phone: String)

open class BaseResponse {
    @SerializedName("error")
    val error: String? = null
}
class RegisterResponse: BaseResponse() {
    @SerializedName("secretUserId")
    val userId: String? = null
}

class ReportFireResponse: BaseResponse() {
    @SerializedName("reportId")
    val reportId: Int? = null
}

data class ImageRequest(val SecretUserId: String)

class UpdateProfileResponse: BaseResponse() {

}