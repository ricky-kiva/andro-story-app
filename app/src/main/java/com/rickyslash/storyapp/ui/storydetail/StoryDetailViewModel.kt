package com.rickyslash.storyapp.ui.storydetail

import android.app.Application
import androidx.lifecycle.ViewModel
import com.rickyslash.storyapp.model.UserModel
import com.rickyslash.storyapp.model.UserSharedPreferences

class StoryDetailViewModel(application: Application): ViewModel() {

    private val userPreferences: UserSharedPreferences = UserSharedPreferences(application)

    fun getPreferences(): UserModel {
        return userPreferences.getUser()
    }

    fun logout() {
        userPreferences.setUser(UserModel())
    }

}