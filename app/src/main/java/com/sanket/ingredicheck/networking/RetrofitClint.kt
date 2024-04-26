package com.sanket.ingredicheck.networking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Interceptor

import java.util.concurrent.TimeUnit

import com.google.gson.GsonBuilder
import android.util.Log

object RetrofitClint {

    private const val BASE_URL = "https://wqidjkpfdrvomfkmefqc.supabase.co/functions/v1/ingredicheck/"
    private const val TAG = "RetrofitClint"
    private var authorizationToken: String? = null

    fun setAuthorizationToken(token: String) {
        authorizationToken = token
    }

    fun clearAuthorizationToken() {
        authorizationToken = null
    }

    // Singleton instance of Retrofit
    private val retrofit: Retrofit by lazy {
        val gson = GsonBuilder().setLenient().create()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(Interceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                authorizationToken?.let {
                    Log.d(TAG, "token: $it")
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }
                chain.proceed(requestBuilder.build())
            })
            .build()

        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun getApi(): Api {
        return retrofit.create(Api::class.java)
    }
}