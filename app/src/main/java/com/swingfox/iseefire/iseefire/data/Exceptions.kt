package com.swingfox.iseefire.iseefire.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class RawErrorResponse {
    @SerializedName("error")
    val error: String? = null
}

class ApiException(
    val code: String,
    val status: Int = -1,
    override val message: String = code
) : RuntimeException() {
    companion object {
        const val CODE_UNKNOWN = "unknown"
        const val CODE_INVALID_RESPONSE = "invalid_response"
    }
}

suspend inline fun <T : Any> Call<T>.awaitResponse(): T {
    return try {
        suspendCancellableCoroutine { continuation ->
            this.enqueue(object : Callback<T> {

                private fun parseErrorBody(errorBody: ResponseBody, status: Int): ApiException {
                    try {
                        return ApiException(
                            code = Gson().fromJson(
                                errorBody.charStream(),
                                RawErrorResponse::class.java
                            ).error ?: ApiException.CODE_UNKNOWN,
                            status = status
                        )
                    } catch (e: Exception) {
                        return ApiException(ApiException.CODE_UNKNOWN)
                    }

                }

                private fun parseError(exception: HttpException): ApiException {
                    return try {
                        exception.response()?.let { resp ->
                            resp.errorBody()?.let { parseErrorBody(it, resp.code()) }
                                ?: ApiException(
                                    ApiException.CODE_UNKNOWN,
                                    resp.code()
                                )
                        }
                            ?: ApiException(ApiException.CODE_UNKNOWN)
                    } catch (ex: Exception) {
                        ApiException(ApiException.CODE_UNKNOWN)
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    if (continuation.isCancelled) return
                    val error = when (t) {
                        is HttpException -> parseError(t)
                        else -> ApiException(
                            code = ApiException.CODE_UNKNOWN,
                            message = t.message
                                ?: ApiException.CODE_UNKNOWN
                        )
                    }
                    continuation.resumeWithException(error)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (continuation.isCancelled) return
                    if (response.isSuccessful) {
                        response.body()?.let { continuation.resume(it) }
                            ?: continuation.resumeWithException(ApiException(ApiException.CODE_INVALID_RESPONSE))
                    } else {
                        val error = try {
                            response.errorBody()?.let { parseErrorBody(it, response.code()) }
                                ?: ApiException(ApiException.CODE_UNKNOWN)
                        } catch (ex: Exception) {
                            ApiException(ApiException.CODE_UNKNOWN)
                        }
                        continuation.resumeWithException(error)
                    }
                }
            })
            continuation.invokeOnCancellation {
                try {
                    this.cancel()
                } catch (ex: Exception) {
                }
            }
        }
    } catch (ex: Exception) {
        ex.fillInStackTrace()
        throw ex
    }
}