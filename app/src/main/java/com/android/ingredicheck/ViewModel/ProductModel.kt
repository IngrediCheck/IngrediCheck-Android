package com.android.ingredicheck.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ingredicheck.BaseClass.Constant
import com.android.ingredicheck.Constant.SupabaseHelp
import com.android.ingredicheck.ResponceModelClass.AnalyzeResponce.AnalyzeResponce
import com.android.ingredicheck.ResponceModelClass.HistoryData.ProductResponce
import com.android.ingredicheck.ui.Views.showToast
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.util.UUID
import java.util.concurrent.TimeUnit


class ProductModel : ViewModel() {

    var productdata = MutableLiveData<ProductResponce?>()
    var fali_api = MutableLiveData<Boolean>()
    var AnalyzeResponcedata = MutableLiveData<AnalyzeResponce?>()
    var is_loder = MutableLiveData<Int>()


    fun get_barcodedata(context: Context, barcode: String, c_id: String) {

        viewModelScope.launch {

            var token = SupabaseHelp().gettoken(context)

            if (token.isEmpty()) {
                showToast(context, "Your token is expired")
                return@launch
            }

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val dispatcher = Dispatcher()
            dispatcher.maxRequests = 1

            val client = OkHttpClient.Builder().addInterceptor(interceptor)
                .dispatcher(dispatcher)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
            val JSON: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body: RequestBody = RequestBody.create(JSON, "{}")
            val request: Request = Request.Builder()
                .url(
                    "https://wqidjkpfdrvomfkmefqc.supabase.co/functions/v1/ingredicheck/inventory/${barcode}?clientActivityId=${c_id}"
                )
                .method("GET", null)
                .addHeader(
                    "Authorization",
                    "Bearer " + token
                )
                .build()

            withContext(Dispatchers.IO) {
                try {
                    val response = client.newCall(request).execute()

                    if (response.isSuccessful && response.body != null) {

                        response.body?.let { responseBody ->
                            var responcedata = Gson().fromJson(
                                responseBody.string(),
                                ProductResponce::class.java
                            )
                            if (responcedata.ingredients != null && responcedata.ingredients!!.size > 0) {
                                productdata.postValue(responcedata)

                                fali_api.postValue(false)
                                get_analyse(context, barcode, c_id)
                            } else {
                                productdata.postValue(null)
                                fali_api.postValue(true)
                            }
                        }
                    } else {
                        productdata.postValue(null)
                        fali_api.postValue(true)
                    }
                } catch (e: Exception) {
                    productdata.postValue(null)
                    fali_api.postValue(true)
                }
                is_loder.postValue(1)
            }
        }
    }


    fun get_imagedata(context: Context, imagedata: String, c_id: String) {

        viewModelScope.launch {

            var token = SupabaseHelp().gettoken(context)

            if (token.isEmpty()) {
                showToast(context, "Your token is expired")
                return@launch
            }

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val dispatcher = Dispatcher()
            dispatcher.maxRequests = 1

            val client = OkHttpClient.Builder().addInterceptor(interceptor)
                .dispatcher(dispatcher)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
            val JSON: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body: RequestBody = okhttp3.MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("clientActivityId", c_id)
                .addFormDataPart("productImages", imagedata)
                .build()

            val request: Request = Request.Builder()
                .url(
                    "https://wqidjkpfdrvomfkmefqc.supabase.co/functions/v1/ingredicheck/extract"
                )
                .method("POST", body)
                .addHeader(
                    "Authorization",
                    "Bearer " + token
                )
                .build()

            withContext(Dispatchers.IO) {
                val response = client.newCall(request).execute()

                if (response.isSuccessful && response.body != null) {
                    response.body?.let { responseBody ->
                        var responcedata = Gson().fromJson(
                            responseBody.string(),
                            ProductResponce::class.java
                        )
                        productdata.postValue(responcedata)

                        fali_api.postValue(false)
                        if (responcedata.ingredients != null && responcedata.ingredients!!.size > 0) {
                            get_analyse(context, "", c_id)
                        }
                    }
                } else {
                    fali_api.postValue(true)
                    productdata.postValue(null)
                }

                is_loder.postValue(1)
            }
        }
    }


    private fun get_analyse(context: Context, barcode: String, clientActivityId: String) {

        viewModelScope.launch {

            var token = SupabaseHelp().gettoken(context)

            if (token.isEmpty()) {
                showToast(context, "Your token is expired")
                return@launch
            }

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val dispatcher = Dispatcher()
            dispatcher.maxRequests = 1

            val client = OkHttpClient.Builder().addInterceptor(interceptor)
                .dispatcher(dispatcher)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            val builder: MultipartBody.Builder =
                okhttp3.MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "clientActivityId", clientActivityId
                    )
                    .addFormDataPart("userPreferenceText", Constant.users_prefrance)
            if (barcode.isNotEmpty()) {
                builder.addFormDataPart("barcode", barcode)
            }
            val body: RequestBody = builder.build()

            var request = Request.Builder()
                .url("https://wqidjkpfdrvomfkmefqc.supabase.co/functions/v1/ingredicheck/analyze")
                .method("POST", body)
                .addHeader(
                    "Authorization",
                    "Bearer " + token
                )
                .build()

            withContext(Dispatchers.IO) {
                val response = client.newCall(request).execute()


                if (response.isSuccessful && response.body != null) {
                    response.body?.let { responseBody ->
                        var responcedata = Gson().fromJson(
                            responseBody.string(),
                            AnalyzeResponce::class.java
                        )
                        AnalyzeResponcedata.postValue(responcedata)
                    }

                } else {
                    AnalyzeResponcedata.postValue(null)
                }
            }
        }
    }

    fun product_historydata(contex: Context, historydata: ProductResponce) {
        try {
            productdata.postValue(historydata)
            is_loder.postValue(1)
            if (historydata.ingredient_recommendations.size > 0) {
                var analyzeresponce = AnalyzeResponce()
                analyzeresponce.addAll(historydata.ingredient_recommendations)
                AnalyzeResponcedata.postValue(analyzeresponce)
            } else {
                get_analyse(contex, historydata.barcode, historydata.client_activity_id)
            }

        } catch (e: Exception) {
        }
    }

    fun addfv(context: Context, clientActivityId: String) {

        viewModelScope.launch {

            var token = SupabaseHelp().gettoken(context)

            if (token.isEmpty()) {
                showToast(context, "Your token is expired")
                return@launch
            }
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val dispatcher = Dispatcher()
            dispatcher.maxRequests = 1

            val client = OkHttpClient.Builder().addInterceptor(interceptor)
                .dispatcher(dispatcher)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            val body: RequestBody = okhttp3.MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("clientActivityId", clientActivityId)
                .build()

            val listId = UUID.fromString("00000000-0000-0000-0000-000000000000").toString()

            val request: Request = Request.Builder()
                .url("https://wqidjkpfdrvomfkmefqc.supabase.co/functions/v1/ingredicheck/lists/" + listId)
                .method("POST", body)
                .addHeader(
                    "Authorization",
                    "Bearer " + token
                )
                .build()

            withContext(Dispatchers.IO) {
                val response = client.newCall(request).execute()

            }
        }

    }

    fun removefv(context: Context, clientActivityId: String) {
        viewModelScope.launch {

            var token = SupabaseHelp().gettoken(context)

            if (token.isEmpty()) {
                showToast(context, "Your token is expired")
                return@launch
            }
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val dispatcher = Dispatcher()
            dispatcher.maxRequests = 1

            val client = OkHttpClient.Builder().addInterceptor(interceptor)
                .dispatcher(dispatcher)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            val listId = UUID.fromString("00000000-0000-0000-0000-000000000000").toString()

            val request: Request = Request.Builder()
                .url(
                    "https://wqidjkpfdrvomfkmefqc.supabase.co/functions/v1/ingredicheck/lists/${listId}/${clientActivityId}"
                )
                .method("DELETE", null)
                .addHeader(
                    "Authorization",
                    "Bearer " + token
                )
                .build()

            withContext(Dispatchers.IO) {
                val response = client.newCall(request).execute()
            }
        }

    }


}
