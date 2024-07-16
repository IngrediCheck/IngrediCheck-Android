package com.android.ingredicheck.Constant

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

class SharePrefrence {

    var sharedPref: SharedPreferences
    var editor: Editor

    constructor(context: Context) {
        sharedPref = context.getSharedPreferences("IngrediCheck", 0)
        editor = sharedPref.edit()
    }


    fun set_boolvalue(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun get_boolvalue(key: String): Boolean {
        return if (!sharedPref.contains(key)) {
            false
        } else {
            sharedPref.getBoolean(key, false)
        }
    }


    fun set_stringvalue(key: String, value: String) {
        editor.putString(key, value)
        editor.commit()
    }

    fun get_stringvalue(key: String): String {
        return sharedPref.getString(key, "") ?: ""
    }


    fun set_intvalue(key: String, value: Int) {
        editor.putInt(key, value)
        editor.commit()
    }

    fun get_intvalue(key: String): Int {
        return sharedPref.getInt(key, 0)
    }

    fun set_openscan(token: Boolean) {
        editor.putBoolean("openscan", token)
        editor.commit()
    }

    fun get_openscan(): Boolean {
        return if (!sharedPref.contains("openscan")) false
         else sharedPref.getBoolean("openscan", false)
    }


    fun set_token(token: String) {
        editor.putString("token", token)
        editor.commit()
    }

    fun get_token(): String {
        return sharedPref.getString("token", "") ?: ""
    }

    fun remove_bykey(key: String) {
        editor.remove(key);
        editor.apply();
    }

    fun clear_Pref() {
        editor.clear()
        editor.commit()
        editor.apply()
    }
}