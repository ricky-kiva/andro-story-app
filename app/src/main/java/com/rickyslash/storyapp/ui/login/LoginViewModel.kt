package com.rickyslash.storyapp.ui.login

import android.util.Log
import androidx.lifecycle.*
import com.rickyslash.storyapp.api.ApiConfig
import com.rickyslash.storyapp.api.response.LoginResponse
import com.rickyslash.storyapp.model.UserModel
import com.rickyslash.storyapp.model.UserPreference
import com.rickyslash.storyapp.ui.main.MainViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference): ViewModel() {

    private val _loggedUser = MutableLiveData<UserModel>()
    val loggedUser: LiveData<UserModel> = _loggedUser

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _loginToken = MutableLiveData<String>()
    val loginToken: LiveData<String> = _loginToken

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _responseMessage = MutableLiveData<String?>()
    val responseMessage: LiveData<String?> = _responseMessage

    fun getUser(token: String = ""): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun loginSuccess(user: UserModel) {
        viewModelScope.launch {
            pref.loginSuccess(user)
        }
    }

    fun userLogin(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().userLogin(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _isError.value = responseBody.error
                        _responseMessage.value = responseBody.message
                        loginSuccess(
                            UserModel(responseBody.loginResult.name, email, true, responseBody.loginResult.token)
                        )
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

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
                _responseMessage.value = t.message
                Log.e(TAG, "onFailure: ${t.message}")
                _responseMessage.value = null
            }
        })
    }

    companion object {
        private val TAG = LoginViewModel::class.java.simpleName
    }

}