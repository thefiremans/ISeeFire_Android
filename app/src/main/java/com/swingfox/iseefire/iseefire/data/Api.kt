package com.swingfox.iseefire.iseefire.data

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface Api {

    @POST("Fire/ReportTheFire")
    fun reportTheFire(@Body reportFire: ReportFireRequest): Call<ReportFireResponse>

    @POST("User/GetSecretUserId")
    fun registerUser(): Call<RegisterResponse>

    @POST("User/UpdateProfile")
    fun updateProfile(@Body updateProfile: UpdateProfileRequest): Call<UpdateProfileResponse>

    @POST("Upload/UploadImage")
    @Multipart
    fun uploadImage(@Part userId: MultipartBody.Part, @Part reportId: MultipartBody.Part, @Part image: MultipartBody.Part): Call<BaseResponse>
}