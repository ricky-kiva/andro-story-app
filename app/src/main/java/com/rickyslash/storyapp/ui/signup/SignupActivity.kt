package com.rickyslash.storyapp.ui.signup

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rickyslash.storyapp.databinding.ActivitySignupBinding
import com.rickyslash.storyapp.helper.ViewModelFactory
import com.rickyslash.storyapp.helper.isValidEmail
import com.rickyslash.storyapp.model.UserPreference
import com.rickyslash.storyapp.ui.login.LoginActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SignupActivity : AppCompatActivity() {

    private lateinit var signupViewModel: SignupViewModel
    private val binding by lazy(LazyThreadSafetyMode.NONE) { ActivitySignupBinding.inflate(layoutInflater) }

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
        signupViewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(dataStore)))[SignupViewModel::class.java]
    }

    private fun setupAction() {
        binding.btnSignup.setOnClickListener {
            val name = binding.edtxName.text.toString()
            val email = binding.edtxEmail.text.toString()
            val password = binding.edtxPass.text.toString()
            when {
                name.isEmpty() -> {
                    binding.edtxLayoutName.error = "Enter your name"
                }
                email.isEmpty() -> {
                    binding.edtxLayoutEmail.error = "Enter your email"
                }
                password.isEmpty() -> {
                    binding.edtxLayoutPass.error = "Enter your password"
                }
                (password.length < 8) -> {
                    Toast.makeText(this@SignupActivity, "Password must be at least 8 characters long.", Toast.LENGTH_SHORT).show()
                }
                (!isValidEmail(email)) -> {
                    Toast.makeText(this@SignupActivity, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    isLoadingObserver = Observer { showLoading(it) }
                    isLoadingObserver?.let {
                        signupViewModel.isLoading.observe(this, it)
                    }
                    signupViewModel.userRegister(name, email, password)
                    isErrorObserver = Observer { isError ->
                        if (!isError) {
                            dialogSignupSuccess(Intent(this@SignupActivity, LoginActivity::class.java))
                        } else {
                            responseMessageObserver = Observer { responseMessage ->
                                if (responseMessage != null) {
                                    Toast.makeText(this, responseMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                            responseMessageObserver?.let {
                                signupViewModel.responseMessage.observeOnce(this, it)
                            }
                        }
                    }
                    isErrorObserver?.let {
                        signupViewModel.isError.observe(this, it)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isErrorObserver?.let(signupViewModel.isError::removeObserver)
        responseMessageObserver?.let(signupViewModel.responseMessage::removeObserver)
        isLoadingObserver?.let(signupViewModel.isLoading::removeObserver)
    }

    private fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
        observe(owner, object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value)
                removeObserver(this)
            }
        })
    }

    private fun dialogSignupSuccess(intent: Intent) {
        AlertDialog.Builder(this).apply {
            setTitle("Success")
            setMessage("Congrats, you've successfully registered! Welcome to the club! \uD83C\uDF89")
            setPositiveButton("Login") { _, _ ->
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.signupProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}