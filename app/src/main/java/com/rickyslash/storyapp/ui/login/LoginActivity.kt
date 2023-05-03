package com.rickyslash.storyapp.ui.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rickyslash.storyapp.databinding.ActivityLoginBinding
import com.rickyslash.storyapp.helper.ViewModelFactory
import com.rickyslash.storyapp.helper.isValidEmail
import com.rickyslash.storyapp.model.UserPreference
import com.rickyslash.storyapp.ui.main.MainActivity
import com.rickyslash.storyapp.ui.signup.SignupActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private val binding by lazy(LazyThreadSafetyMode.NONE) { ActivityLoginBinding.inflate(layoutInflater) }

    private var isErrorObserver: Observer<Boolean>? = null
    private var responseMessageObserver: Observer<String?>? = null
    private var isLoadingObserver: Observer<Boolean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
    }

    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(dataStore)))[LoginViewModel::class.java]
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edtxEmail.text.toString()
            val password = binding.edtxPass.text.toString()
            when {
                email.isEmpty() -> {
                    binding.edtxLayoutEmail.error = "Enter your email"
                }
                password.isEmpty() -> {
                    binding.edtxLayoutPass.error = "Enter your password"
                }
                (!isValidEmail(email)) -> {
                    Toast.makeText(this@LoginActivity, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    isLoadingObserver = Observer { showLoading(it) }
                    isLoadingObserver?.let {
                        loginViewModel.isLoading.observe(this, it)
                    }
                    loginViewModel.userLogin(email, password)
                    isErrorObserver = Observer { isError ->
                        if (!isError) {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            responseMessageObserver = Observer { responseMessage ->
                                if (responseMessage != null) {
                                    Toast.makeText(this, responseMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                            responseMessageObserver?.let {
                                loginViewModel.responseMessage.observeOnce(this, it)
                            }
                        }
                    }
                    isErrorObserver?.let {
                        loginViewModel.isError.observe(this, it)
                    }
                }
            }
        }
        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isErrorObserver?.let(loginViewModel.isError::removeObserver)
        responseMessageObserver?.let(loginViewModel.responseMessage::removeObserver)
        isLoadingObserver?.let(loginViewModel.isLoading::removeObserver)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loginProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
        observe(owner, object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value)
                removeObserver(this)
            }
        })
    }

}