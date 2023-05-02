package com.rickyslash.storyapp.ui.addstory

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.rickyslash.storyapp.R
import com.rickyslash.storyapp.databinding.ActivityAddStoryBinding
import com.rickyslash.storyapp.helper.ViewModelFactory
import com.rickyslash.storyapp.helper.rotateAndCompressFile
import com.rickyslash.storyapp.model.UserPreference
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddStoryActivity : AppCompatActivity() {

    private lateinit var addStoryViewModel: AddStoryViewModel
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityAddStoryBinding.inflate(layoutInflater)
    }

    private var getFile: File? = null

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
                rotateAndCompressFile(file, isBackCamera)
                getFile = file
                binding.ivPreviewImg.setImageBitmap(BitmapFactory.decodeFile(file.path))
                binding.tvPreviewImgText.visibility = View.GONE
                binding.stAddFlPlaceholder.setOnClickListener(null)
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
    }

    private fun setupAction() {
        binding.ibCamera.setOnClickListener { startCameraX() }
        binding.stAddFlPlaceholder.setOnClickListener { startCameraX() }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
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

    companion object {
        const val CAMERA_X_RESULT = 200
    }
}