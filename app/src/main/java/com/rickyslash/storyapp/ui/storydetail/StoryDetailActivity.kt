package com.rickyslash.storyapp.ui.storydetail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
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
import com.bumptech.glide.Glide
import com.rickyslash.storyapp.R
import com.rickyslash.storyapp.api.response.ListStoryItem
import com.rickyslash.storyapp.databinding.ActivityStoryDetailBinding
import com.rickyslash.storyapp.helper.ViewModelFactory
import com.rickyslash.storyapp.helper.formatDate
import com.rickyslash.storyapp.helper.getRandomMaterialColor
import com.rickyslash.storyapp.helper.titleSentence
import com.rickyslash.storyapp.model.UserPreference
import com.rickyslash.storyapp.ui.login.LoginActivity
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var storyDetailViewModel: StoryDetailViewModel
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityStoryDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupViewComponent()
    }

    private fun setupView() {
        supportActionBar?.apply {
            val text = SpannableString(supportActionBar?.title)
            text.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@StoryDetailActivity, R.color.black)), 0, text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            elevation = "0".toFloat()
            setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@StoryDetailActivity, R.color.teal_EDC)))
            title = text
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        }
    }

    private fun setupViewModel() {
        storyDetailViewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(dataStore)))[StoryDetailViewModel::class.java]
    }

    private fun setupViewComponent() {
        val data = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_STORY_ITEM, ListStoryItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_STORY_ITEM)
        }
        if (data != null) {
            binding.tvFromName.text = getString(R.string.greet_name, titleSentence(data.name))
            binding.tvName.text = data.name
            binding.tvDate.text = formatDate(data.createdAt)
            binding.tvDesc.text = data.description
            Glide.with(this@StoryDetailActivity)
                .load(data.photoUrl)
                .placeholder(ColorDrawable(getRandomMaterialColor()))
                .into(binding.ivDetailImg)
        }
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
                storyDetailViewModel.logout()
                val intent = Intent(this@StoryDetailActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> true
        }
    }

    companion object {
        const val EXTRA_STORY_ITEM = "extra_username"
    }
}