package com.swingfox.iseefire.iseefire.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log

class PreferenceUtil(context: Context) {

    val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getString(key: String) = prefs.getString(key, "")

    fun setString(key: String, value: String): Boolean {
        val editor = prefs.edit()
        editor.putString(key, value)
        return editor.commit()
    }
}