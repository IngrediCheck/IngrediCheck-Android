package com.android.ingredicheck.BaseClass

import android.app.Application
import android.widget.Toast
import androidx.compose.ui.res.stringResource
import com.android.ingredicheck.R
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage


class MyApplication : Application() {


    companion object {
        lateinit var instance: MyApplication


        fun getAppInstance(): MyApplication {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

    }

    fun getClient(): io.github.jan.supabase.SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = instance.getString(R.string.supa_url),
            supabaseKey = instance.getString(R.string.supa_token)
        ) {
            install(Postgrest)
        }


    }




    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun getAppVersion(): String? {
        return this.packageManager.getPackageInfo(this.packageName, 0).versionName
    }
}