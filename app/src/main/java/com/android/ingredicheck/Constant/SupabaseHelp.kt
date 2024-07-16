package com.android.ingredicheck.Constant

import android.content.Context
import com.android.ingredicheck.R
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.user.UserSession
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

class SupabaseHelp {

    companion object {
        public var userSession: UserSession? = null
        public var supabase: SupabaseClient? = null
    }

    fun get_supaclient (context: Context): SupabaseClient? {

        if(supabase == null)
        {
            supabase = createSupabaseClient(
                supabaseUrl = context.getString(R.string.supa_url) ,
                supabaseKey =context.getString(R.string.supa_token)
            ) {
                install(Storage) {
                }
                install(Auth) {
                }
                install(ComposeAuth) {
                }
            }

            return supabase
        }else
        {
            return supabase
        }


    }

    fun supabaseclient (context: Context) {

        supabase = createSupabaseClient(
            supabaseUrl = context.getString(R.string.supa_url) ,
            supabaseKey =context.getString(R.string.supa_token)
        ) {
            install(Storage) {
            }
            install(Auth) {
            }
            install(ComposeAuth) {
            }

        }


    }

    public fun gettoken(context: Context): String {

        try {
            if (userSession == null) {
                var supabase = createSupabaseClient(
                    supabaseUrl = context.getString(R.string.supa_url),
                    supabaseKey = context.getString(R.string.supa_token)
                ) {

                    install(Auth) {
                    }
                }
                SupabaseHelp.userSession = supabase.auth.currentSessionOrNull()
            }

            return if (userSession == null) ""
            else userSession!!.accessToken

        } catch (e: Exception) {

            return ""
        }
    }
}