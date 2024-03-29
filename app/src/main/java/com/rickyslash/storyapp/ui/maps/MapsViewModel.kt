package com.rickyslash.storyapp.ui.maps

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.rickyslash.storyapp.api.ApiConfig
import com.rickyslash.storyapp.api.response.AllStoriesResponse
import com.rickyslash.storyapp.api.response.ListStoryItem
import com.rickyslash.storyapp.model.UserModel
import com.rickyslash.storyapp.model.UserSharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(application: Application): ViewModel() {

    private val userPreferences: UserSharedPreferences = UserSharedPreferences(application)

    private val _listStoryItem = MutableLiveData<List<ListStoryItem>>()
    val listStoryItem: LiveData<List<ListStoryItem>> = _listStoryItem

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _responseMessage = MutableLiveData<String?>()
    val responseMessage: LiveData<String?> = _responseMessage

    fun getPreferences(): UserModel {
        return userPreferences.getUser()
    }

    fun logout() {
        userPreferences.setUser(UserModel())
    }

    fun getStories() {
        _isLoading.value = true
        val client = ApiConfig.getApiService(userPreferences).getStories(1, 25, 1)
        client.enqueue(object : Callback<AllStoriesResponse> {
            override fun onResponse(
                call: Call<AllStoriesResponse>,
                response: Response<AllStoriesResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _isError.value = responseBody.error
                        _responseMessage.value = responseBody.message
                        _listStoryItem.value = responseBody.listStory
                        _responseMessage.value = null
                    }
                } else {
                    _isLoading.value = false
                    _isError.value = true
                    _responseMessage.value = response.message()
                    Log.e(TAG, "isNotSuccessful: ${response.message()}")
                    _responseMessage.value = null
                }
            }

            override fun onFailure(call: Call<AllStoriesResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
                _responseMessage.value = t.message
                Log.e(TAG, "onFailure: ${t.message}")
                _responseMessage.value = null
            }
        })
    }

    companion object {
        private val TAG = MapsViewModel::class.java.simpleName
    }

}