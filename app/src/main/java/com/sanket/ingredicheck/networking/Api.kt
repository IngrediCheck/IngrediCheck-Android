package com.sanket.ingredicheck.networking

import com.sanket.ingredicheck.model.AddDietary
import com.sanket.ingredicheck.model.Dietary
import com.sanket.ingredicheck.response.PreferenceResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface Api {


    @GET("preferencelists/default")
    suspend fun getPreferenceLists(): Response<List<Dietary>>



    @POST("preferencelists/default")
    suspend fun addPreference(@Body body: RequestBody): Response<PreferenceResponse>
}