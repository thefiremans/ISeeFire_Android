package com.swingfox.iseefire.iseefire.domain

import com.swingfox.iseefire.iseefire.util.PreferenceUtil

class Repository(private val preferences: PreferenceUtil) {

    private val USER_ID_KEY = "user_id"
    var userId: String
        get() = preferences.getString(USER_ID_KEY)
        set(value) {
            preferences.setString(USER_ID_KEY, value)
        }

}