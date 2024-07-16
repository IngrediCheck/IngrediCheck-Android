package com.android.ingredicheck.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ingredicheck.Constant.SupabaseHelp
import com.android.ingredicheck.ResponceModelClass.HistoryData.Productdata
import com.android.ingredicheck.ui.Views.showToast
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.util.UUID
import java.util.concurrent.TimeUnit

class GetHistoryModel : ViewModel() {

    var historydata = MutableLiveData<Productdata?>()
    var fvlist = MutableLiveData<Productdata?>()
    var is_loder = MutableLiveData<Int>()

    fun get_history(context: Context, isloder: Boolean) {

        viewModelScope.launch {


            try {

                var token = SupabaseHelp().gettoken(context)

                if (token.isEmpty()) {
                    showToast(context, "Your token is expired")
                    return@launch
                }
                if (isloder) is_loder.postValue(1)
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
                        "https://wqidjkpfdrvomfkmefqc.supabase.co/functions/v1/ingredicheck/history"
                    )
                    .method("GET", null)
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
                                Productdata::class.java
                            )
                            var proempty = Productdata()
                            historydata.postValue(proempty)
                            historydata.postValue(responcedata)
                            is_loder.postValue(0)
                        }

                    } else {
                        is_loder.postValue(0)
                        historydata.postValue(null)
                    }

                }
            } catch (e: Exception) {
                is_loder.postValue(0)
            }
        }
    }


    fun get_fvlistdata(context: Context, withloder: Boolean) {

        viewModelScope.launch {
            try {

                var token = SupabaseHelp().gettoken(context)

                if (token.isEmpty()) {
                    showToast(context, "Your token is expired")
                    return@launch
                }

                if (withloder) is_loder.postValue(1)
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
                        "https://wqidjkpfdrvomfkmefqc.supabase.co/functions/v1/ingredicheck/lists/${listId}"
                    )
                    .method("GET", null)
                    .addHeader(
                        "Authorization",
                        "Bearer " + token
                    )
                    .build()

                withContext(Dispatchers.IO) {
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful && response.body != null) {
                        response.body?.let { responseBody ->

                            response.body?.let { responseBody ->
                                var responcedata = Gson().fromJson(
                                    responseBody.string(),
                                    Productdata::class.java
                                )
                                var proempty = Productdata()
                                fvlist.postValue(proempty)
                                responcedata.forEach {
                                    it.favorited = true
                                    it.client_activity_id = it.list_item_id
                                }

                                fvlist.postValue(responcedata)
                                is_loder.postValue(0)
                            }
                        }
                    } else {
                        is_loder.postValue(0)
                    }
                }
            } catch (e: Exception) {
                is_loder.postValue(0)
            }

        }

    }

    fun get_serachdata(context: Context, text: String) {

        viewModelScope.launch {
            try {

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

                val request: Request = Request.Builder()
                    .url(
                        "https://wqidjkpfdrvomfkmefqc.supabase.co/functions/v1/ingredicheck/history?searchText=${text}"
                    )
                    .method("GET", null)
                    .addHeader(
                        "Authorization",
                        "Bearer " + token
                    )
                    .build()

                withContext(Dispatchers.IO) {
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful && response.body != null) {
                        response.body?.let { responseBody ->

                            response.body?.let { responseBody ->
                                var responcedata = Gson().fromJson(
                                    responseBody.string(),
                                    Productdata::class.java
                                )
                                historydata.postValue(responcedata)
                            }
                        }
                    } else {
                    }
                }
            } catch (e: Exception) {
            }

        }

    }
}