package com.rickyslash.storyapp.model

import android.content.Context

class UserSharedPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setUser(value: UserModel) {
        val editor = preferences.edit()
        editor.putString(NAME, value.name)
        editor.putString(EMAIL, value.email)
        editor.putBoolean(IS_LOGIN, value.isLogin)
        editor.putString(TOKEN, value.token)
        editor.apply()
    }

    fun getUser(): UserModel {
        val model = UserModel()
        model.name = preferences.getString(NAME, "")
        model.email = preferences.getString(EMAIL, "")
        model.isLogin = preferences.getBoolean(IS_LOGIN, false)
        model.token = preferences.getString(TOKEN, "")

        return model
    }

    companion object {
        private const val PREFS_NAME = "user_preferences"
        private const val NAME = "name"
        private const val EMAIL = "email"
        private const val IS_LOGIN = "isLogin"
        private const val TOKEN = "token"
    }

}