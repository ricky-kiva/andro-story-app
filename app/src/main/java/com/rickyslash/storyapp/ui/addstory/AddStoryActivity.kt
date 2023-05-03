package com.rickyslash.storyapp.ui.addstory

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rickyslash.storyapp.R
import com.rickyslash.storyapp.databinding.ActivityAddStoryBinding
import com.rickyslash.storyapp.helper.*
import com.rickyslash.storyapp.model.UserPreference
import com.rickyslash.storyapp.ui.login.LoginActivity
import com.rickyslash.storyapp.ui.main.MainActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddStoryActivity : AppCompatActivity() {

    private lateinit var addStoryViewModel: AddStoryViewModel
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityAddStoryBinding.inflate(layoutInflater)
    }

    private var isErrorObserver: Observer<Boolean>? = null
    private var responseMessageObserver: Observer<String?>? = null
    private var isLoadingObserver: Observer<Boolean>? = null

    private var fileToUpload: File? = null

    private val launcherIntentCameraX = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            myFile?.let { file ->
                rotateImageFile(file, isBackCamera)
                fileToUpload = file
                binding.ivPreviewImg.setImageBitmap(BitmapFactory.decodeFile(file.path))
                imageContainerFilled()
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val selectedImg = it.data?.data as Uri
            selectedImg.let { uri ->
                fileToUpload = uriToFile(uri, this@AddStoryActivity)
                binding.ivPreviewImg.setImageURI(uri)
                imageContainerFilled()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
    }

    private fun setupView() {
        supportActionBar?.apply {
            val text = SpannableString(supportActionBar?.title)
            text.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@AddStoryActivity, R.color.black)), 0, text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            elevation = 0f
            setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@AddStoryActivity, R.color.teal_EDC)))
            title = text
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        }
    }

    private fun setupViewModel() {
        addStoryViewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(dataStore)))[AddStoryViewModel::class.java]
        observeLoading()
        addStoryViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                binding.tvGreetName.text = getString(R.string.greet_name, titleSentence(user.name))
                binding.ibUpload.setOnClickListener { uploadStory(user.token) }
            } else {
                val intent = Intent(this@AddStoryActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.stAddFlPlaceholder.setOnClickListener { startCameraX() }
        binding.ibCamera.setOnClickListener { startCameraX() }
        binding.ibGallery.setOnClickListener { startGallery() }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"

        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun imageContainerFilled() {
        binding.tvPreviewImgText.visibility = View.GONE
        binding.stAddFlPlaceholder.setOnClickListener(null)
    }

    private fun uploadStory(token: String) {
        if (fileToUpload != null) {
            if (binding.edtxDesc.text?.isNotBlank() == true) {
                val file = reduceFileImage(fileToUpload as File)
                val desc = binding.edtxDesc.text.toString().toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("photo", file.name, requestImageFile)
                dialogOnUpload(token, imageMultipart, desc)
            } else {
                Toast.makeText(this@AddStoryActivity, "Please add a description", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@AddStoryActivity, "Please add an image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processUploadStory(token: String, file: MultipartBody.Part, desc: RequestBody) {
        addStoryViewModel.uploadStory(token, file, desc)
        isErrorObserver = Observer { isError ->
            if (!isError) {
                Toast.makeText(this, "Posted! \uD83C\uDF89", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
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
                    addStoryViewModel.responseMessage.observeOnce(this, it)
                }
            }
        }
        isErrorObserver?.let {
            addStoryViewModel.isError.observe(this, it)
        }
    }

    private fun dialogOnUpload(token: String, file: MultipartBody.Part, desc: RequestBody) {
        AlertDialog.Builder(this).apply {
            setTitle("\uD83D\uDE80 Story Launch")
            setMessage("Ready to drop your epic story to the world?")
            setPositiveButton("Yes") { _, _ ->
                processUploadStory(token, file, desc)
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun observeLoading() {
        isLoadingObserver = Observer { showLoading(it) }
        isLoadingObserver?.let {
            addStoryViewModel.isLoading.observe(this, it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.ibUpload.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.minimal_menu, menu)
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_logout -> {
                addStoryViewModel.logout()
                true
            }
            else -> true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isErrorObserver?.let(addStoryViewModel.isError::removeObserver)
        responseMessageObserver?.let(addStoryViewModel.responseMessage::removeObserver)
        isLoadingObserver?.let(addStoryViewModel.isLoading::removeObserver)
    }

    private fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
        observe(owner, object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value)
                removeObserver(this)
            }
        })
    }

    companion object {
        const val CAMERA_X_RESULT = 200
    }
}