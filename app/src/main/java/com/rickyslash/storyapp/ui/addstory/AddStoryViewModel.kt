package com.rickyslash.storyapp.ui.addstory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rickyslash.storyapp.model.UserPreference
import kotlinx.coroutines.launch

class AddStoryViewModel(private val pref: UserPreference): ViewModel() {

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

}