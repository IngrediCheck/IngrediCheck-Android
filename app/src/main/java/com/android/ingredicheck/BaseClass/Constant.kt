package com.android.ingredicheck.BaseClass

import io.github.jan.supabase.gotrue.user.UserInfo
import java.util.UUID

class Constant {
    companion object {
        var user_data: UserInfo? = null
        var users_prefrance: String = ""

         var isBarcodeDetected = false
        fun getclientid (): String {
           return UUID.randomUUID().toString()
        }
    }

}