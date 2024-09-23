package com.rickyslash.storyapp.helper

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rickyslash.storyapp.helper.di.Injection
import com.rickyslash.storyapp.ui.addstory.AddStoryViewModel
import com.rickyslash.storyapp.ui.login.LoginViewModel
import com.rickyslash.storyapp.ui.main.MainViewModel
import com.rickyslash.storyapp.ui.maps.MapsViewModel
import com.rickyslash.storyapp.ui.signup.SignupViewModel
import com.rickyslash.storyapp.ui.storydetail.StoryDetailViewModel

class ViewModelFactory(private val application: Application): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(Injection.providePreferences(application), Injection.provideRepository(application)) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(Injection.providePreferences(application)) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(Injection.providePreferences(application)) as T
            }
            modelClass.isAssignableFrom(StoryDetailViewModel::class.java) -> {
                StoryDetailViewModel(Injection.providePreferences(application)) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(Injection.providePreferences(application)) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(Injection.providePreferences(application)) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}