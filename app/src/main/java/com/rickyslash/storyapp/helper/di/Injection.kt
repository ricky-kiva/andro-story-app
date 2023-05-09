package com.rickyslash.storyapp.helper.di

import android.content.Context
import com.rickyslash.storyapp.api.ApiConfig
import com.rickyslash.storyapp.data.StoryRepository
import com.rickyslash.storyapp.model.UserSharedPreferences

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val userSharedPreferences = UserSharedPreferences(context)
        val apiService = ApiConfig.getApiService(userSharedPreferences)
        return StoryRepository(apiService)
    }
}