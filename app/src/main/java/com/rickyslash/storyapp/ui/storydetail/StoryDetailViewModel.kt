package com.rickyslash.storyapp.ui.storydetail

import androidx.lifecycle.ViewModel
import com.rickyslash.storyapp.model.UserModel
import com.rickyslash.storyapp.model.UserSharedPreferences

class StoryDetailViewModel(private val userPreferences: UserSharedPreferences): ViewModel() {

    fun getPreferences(): UserModel {
        return userPreferences.getUser()
    }

    fun logout() {
        userPreferences.setUser(UserModel())
    }

}