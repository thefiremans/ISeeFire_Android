package com.swingfox.iseefire.iseefire.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiProvider {

    private val HOST = "swingfox.com"
    private val HTTP_SCHEME = "http"
    val api: Api

    init {
        val retrofit = buildRetrofit(buildClient(), buildUrl())
        api = retrofit.create(Api::class.java)
    }

    private fun buildRetrofit(client: OkHttpClient, baseUrl: HttpUrl): Retrofit {
        val builder = Retrofit.Builder()
        builder.client(client)
        builder.baseUrl(baseUrl)
        builder.addConverterFactory(GsonConverterFactory.create(buildGson()))
        return builder.build()
    }

    private fun buildGson(): Gson {
        return GsonBuilder().create()
    }

    private fun buildUrl(): HttpUrl {
        return HttpUrl.Builder().host(HOST).addPathSegments("ISeeFire/").scheme(HTTP_SCHEME).build()
    }


    private fun buildClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        return builder.build()
    }
}