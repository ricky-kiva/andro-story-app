package com.rickyslash.storyapp.ui.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rickyslash.storyapp.R
import com.rickyslash.storyapp.databinding.ActivitySignupBinding
import com.rickyslash.storyapp.helper.isValidEmail
import com.rickyslash.storyapp.ui.login.LoginActivity

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
        signupViewModel = ViewModelProvider(this)[SignupViewModel::class.java]
    }

    private fun setupAction() {
        binding.btnSignup.setOnClickListener {
            val name = binding.edtxName.text.toString()
            val email = binding.edtxEmail.text.toString()
            val password = binding.edtxPass.text.toString()
            when {
                name.isEmpty() -> {
                    binding.edtxLayoutName.error = getString(R.string.ask_enter_name)
                }
                email.isEmpty() -> {
                    binding.edtxLayoutEmail.error = getString(R.string.ask_enter_email)
                }
                password.isEmpty() -> {
                    binding.edtxLayoutPass.error = getString(R.string.ask_enter_pass)
                }
                (password.length < 8) -> {
                    Toast.makeText(this@SignupActivity, getString(R.string.warn_pass_8_char), Toast.LENGTH_SHORT).show()
                }
                (!isValidEmail(email)) -> {
                    Toast.makeText(this@SignupActivity, getString(R.string.wrong_format_email), Toast.LENGTH_SHORT).show()
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
                        }
                    }
                    responseMessageObserver = Observer { responseMessage ->
                        if (responseMessage != null && signupViewModel.isError.value == true) {
                            Toast.makeText(this, responseMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                    isErrorObserver?.let { signupViewModel.isError.observe(this, it) }
                    responseMessageObserver?.let { signupViewModel.responseMessage.observe(this, it) }
                }
            }
        }
    }

    private fun dialogSignupSuccess(intent: Intent) {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.success))
            setMessage(getString(R.string.signup_success_desc))
            setPositiveButton(getString(R.string.login)) { _, _ ->
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

    override fun onDestroy() {
        super.onDestroy()
        isErrorObserver?.let(signupViewModel.isError::removeObserver)
        responseMessageObserver?.let(signupViewModel.responseMessage::removeObserver)
        isLoadingObserver?.let(signupViewModel.isLoading::removeObserver)
    }

}