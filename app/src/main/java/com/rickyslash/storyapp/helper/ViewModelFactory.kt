package com.rickyslash.storyapp.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rickyslash.storyapp.model.UserPreference
import com.rickyslash.storyapp.ui.addstory.AddStoryActivity
import com.rickyslash.storyapp.ui.addstory.AddStoryViewModel
import com.rickyslash.storyapp.ui.login.LoginViewModel
import com.rickyslash.storyapp.ui.main.MainViewModel
import com.rickyslash.storyapp.ui.signup.SignupViewModel
import com.rickyslash.storyapp.ui.storydetail.StoryDetailViewModel

class ViewModelFactory(private val pref: UserPreference): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(pref) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            modelClass.isAssignableFrom(StoryDetailViewModel::class.java) -> {
                StoryDetailViewModel(pref) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}