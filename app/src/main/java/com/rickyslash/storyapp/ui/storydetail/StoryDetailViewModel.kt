package com.rickyslash.storyapp.ui.storydetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rickyslash.storyapp.model.UserPreference
import kotlinx.coroutines.launch

class StoryDetailViewModel(private val pref: UserPreference): ViewModel() {

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

}