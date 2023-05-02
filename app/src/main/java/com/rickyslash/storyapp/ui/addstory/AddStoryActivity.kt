package com.rickyslash.storyapp.ui.addstory

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.rickyslash.storyapp.R
import com.rickyslash.storyapp.databinding.ActivityAddStoryBinding
import com.rickyslash.storyapp.helper.ViewModelFactory
import com.rickyslash.storyapp.model.UserPreference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddStoryActivity : AppCompatActivity() {

    private lateinit var addStoryViewModel: AddStoryViewModel
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityAddStoryBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupViewModel()
    }

    private fun setupView() {
        supportActionBar?.apply {
            val text = SpannableString(supportActionBar?.title)
            text.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@AddStoryActivity, R.color.black)), 0, text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            elevation = "0".toFloat()
            setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@AddStoryActivity, R.color.teal_EDC)))
            title = text
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        }
    }

    private fun setupViewModel() {
        addStoryViewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(dataStore)))[AddStoryViewModel::class.java]
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
}