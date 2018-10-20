package com.swingfox.iseefire.iseefire.domain

import com.swingfox.iseefire.iseefire.data.*
import com.swingfox.iseefire.iseefire.data.awaitResponse
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import kotlinx.coroutines.experimental.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ApiInteractor {

    private val api = ApiProvider().api
    private val dispatcher = newFixedThreadPoolContext(2, "PopupRepository")

    suspend fun reportFire(userId: String): ReportFireResponse = withContext(dispatcher) {
        return@withContext api.reportTheFire(ReportFireRequest(userId)).awaitResponse()
    }

    suspend fun registerUser(): RegisterResponse = withContext(dispatcher) {
        return@withContext api.registerUser().awaitResponse()
    }

    suspend fun updateProfile(userId: String, name: String, phone: String): UpdateProfileResponse =
        withContext(dispatcher) {
            return@withContext api.updateProfile(UpdateProfileRequest(userId, name, phone)).awaitResponse()
        }

    suspend fun uploadImage(userId: String, reportId: String, path: String): BaseResponse = withContext(dispatcher) {
        val body = RequestBody.create(MediaType.parse("image/jpg"), File(path))
        val part = MultipartBody.Part.createFormData("Image", "image.jpg", body)
        val userIdPart = MultipartBody.Part.createFormData("SecretUserId", userId)
        val reportId = MultipartBody.Part.createFormData("ReportId", reportId)
        return@withContext api.uploadImage(userIdPart, reportId, part).awaitResponse()
    }

}