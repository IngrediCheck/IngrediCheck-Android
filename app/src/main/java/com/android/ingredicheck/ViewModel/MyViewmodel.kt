package com.android.ingredicheck.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ingredicheck.BaseClass.Constant
import com.android.ingredicheck.Constant.SupabaseHelp
import com.android.ingredicheck.ResponceModelClass.PreferencelistsModel.PreferencelistsModedataItem
import com.android.ingredicheck.ResponceModelClass.PreferencelistsModel.PreferencelistsModel
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
import java.util.concurrent.TimeUnit


class MyViewmodel : ViewModel() {

    var prefhdata = MutableLiveData<ArrayList<PreferencelistsModedataItem>?>()
    var is_loder = MutableLiveData<Int>()
    var is_delete = MutableLiveData<Boolean>()


    fun chnage_loader(i: Int) {
        is_loder.postValue(i)
    }

    fun fatchdata(context: Context) {

        viewModelScope.launch {

            var token = SupabaseHelp().gettoken(context)

            if (token.isEmpty()) {
                showToast(context, "Your token is expired")
                return@launch
            }

            try {
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
                        "https://wqidjkpfdrvomfkmefqc.supabase.co/functions/v1/ingredicheck/preferencelists/default"
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
                                PreferencelistsModel::class.java
                            )
                            Log.e("mydataistextis","  is_removetext.postVa is =--  ")
                            prefhdata.postValue(responcedata)

                        }

                    } else {
                        prefhdata.postValue(null)
                    }

                }
            } catch (e: Exception) {
            }

            // is_loder.postValue(1)
            /*RetrofitInstant().retrofit().create(ApiClass::class.java)
                .get_preferencelists(
                    "Bearer " + SharePrefrence(context).get_token()
                ).enqueue(object : Callback<PreferencelistsModel> {
                    override fun onResponse(
                        call: Call<PreferencelistsModel>,
                        response: Response<PreferencelistsModel>
                    ) {

                        if (response.isSuccessful && response.body() != null) {
                            prefhdata.postValue(response.body())
                            //  is_loder.postValue(0)
                        } else {
                            prefhdata.postValue(null)
                            //   is_loder.postValue(0)
                        }
                    }

                    override fun onFailure(
                        call: Call<PreferencelistsModel>,
                        t: Throwable
                    ) {
                        Log.e("mydatachnage", "message   is ==  " + t.message)

                        prefhdata.postValue(null)
                        //   is_loder.postValue(0)
                    }
                })*/
        }
    }

    fun add_data(context: Context, text: String) {

        viewModelScope.launch {
            try {

                var token = SupabaseHelp().gettoken(context)

                if (token.isEmpty()) {
                    showToast(context, "Your token is expired")
                    return@launch
                }

                is_loder.postValue(1)
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

                /*val client = OkHttpClient().newBuilder()
                    .build()*/
                val mediaType: MediaType = "text/plain".toMediaTypeOrNull()!!
                val body: RequestBody = okhttp3.MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("clientActivityId", Constant.getclientid())
                    .addFormDataPart("preference", text.trim())
                    .build()
                val request: Request = Request.Builder()
                    .url("https://wqidjkpfdrvomfkmefqc.supabase.co/functions/v1/ingredicheck/preferencelists/default")
                    .method("POST", body)
                    .addHeader(
                        "Authorization",
                        "Bearer " + token
                    )
                    .build()

                withContext(Dispatchers.IO) {

                    val response = client.newCall(request).execute()

                    if (response.body != null) {
                        response.body?.let { responseBody ->
                            var responcedata = Gson().fromJson(
                                responseBody.string(),
                                PreferencelistsModedataItem::class.java
                            )
                            if (responcedata.result.equals("success")) {
                                var peoductdata = ArrayList<PreferencelistsModedataItem>()
                                if (prefhdata.value != null) peoductdata.addAll(prefhdata.value!!)
                                peoductdata.add(0, responcedata)
                                prefhdata.postValue(peoductdata)
                                is_loder.postValue(3)
                            } else {
                                is_loder.postValue(2)
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

    fun deletedata(context: Context, id: Int) {

        viewModelScope.launch {
            //      is_loder.postValue(1)

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

            /*val client = OkHttpClient().newBuilder()
                .build()*/
            val mediaType: MediaType = "text/plain".toMediaTypeOrNull()!!
            val body: RequestBody = okhttp3.MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("clientActivityId", Constant.getclientid())
                .build()
            val request: Request = Request.Builder()
                .url("https://wqidjkpfdrvomfkmefqc.supabase.co/functions/v1/ingredicheck/preferencelists/default/" + id)
                .method("DELETE", body)
                .addHeader(
                    "Authorization",
                    "Bearer " + token
                )
                .build()
            withContext(Dispatchers.IO) {
                val response = client.newCall(request).execute()
                if (response.code == 204) {
                    fatchdata(context)
                    //    is_loder.postValue(0)
                } else {
                    //     is_loder.postValue(0)
                }
            }
        }
    }

    fun edit_data(context: Context, text: String, selectId: Int) {

        viewModelScope.launch {

            try {

                var token = SupabaseHelp().gettoken(context)

                if (token.isEmpty()) {
                    showToast(context, "Your token is expired")
                    return@launch
                }

                is_loder.postValue(1)

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

                /*val client = OkHttpClient().newBuilder()
                    .build()*/
                val mediaType: MediaType = "text/plain".toMediaTypeOrNull()!!
                val body: RequestBody = okhttp3.MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("clientActivityId", Constant.getclientid())
                    .addFormDataPart("preference", text)
                    .build()
                val request: Request = Request.Builder()
                    .url("https://wqidjkpfdrvomfkmefqc.supabase.co/functions/v1/ingredicheck/preferencelists/default/" + selectId)
                    .method("PUT", body)
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
                                PreferencelistsModedataItem::class.java
                            )
                            if (responcedata.result.equals("success")) {
                                fatchdata(context)
                                is_loder.postValue(3)
                            } else {
                                is_loder.postValue(2)
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

    fun feedback(context: Context, feedbackdatast: String, client_id: String?) {

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

            /*val client = OkHttpClient().newBuilder()
                .build()*/
            val mediaType: MediaType = "text/plain".toMediaTypeOrNull()!!
            val body: RequestBody = okhttp3.MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("clientActivityId", client_id!!)
                .addFormDataPart("feedback", feedbackdatast)
                .build()

            val request: Request = Request.Builder()
                .url("https://wqidjkpfdrvomfkmefqc.supabase.co/functions/v1/ingredicheck/feedback")
                .method("POST", body)
                .addHeader(
                    "Authorization",
                    "Bearer " + token
                )
                .build()

            withContext(Dispatchers.IO) {
                val response: okhttp3.Response = client.newCall(request).execute()
            }


        }
    }

    fun delete_me(context: Context) {

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
            val mediaType = "text/plain".toMediaTypeOrNull()
            val body = RequestBody.create(mediaType, "")

            val client = OkHttpClient.Builder().addInterceptor(interceptor)
                .dispatcher(dispatcher)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            /*val client = OkHttpClient().newBuilder()
                .build()*/


            val request: Request = Request.Builder()
                .url("https://wqidjkpfdrvomfkmefqc.supabase.co/functions/v1/ingredicheck/deleteme")
                .method("POST", body)
                .addHeader(
                    "Authorization",
                    "Bearer " + token
                )
                .build()
            withContext(Dispatchers.IO) {
                val response: okhttp3.Response = client.newCall(request).execute()
                is_delete.postValue(true)
            }
        }
    }

}