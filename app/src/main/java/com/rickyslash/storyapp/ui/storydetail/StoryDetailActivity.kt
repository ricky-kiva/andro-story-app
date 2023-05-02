package com.rickyslash.storyapp.ui.storydetail

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
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
import com.rickyslash.storyapp.model.UserPreference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

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
            binding.tvFromName.text = getString(R.string.greet_name, data.name)
            binding.tvName.text = data.name
            binding.tvDate.text = formatDate(data.createdAt)
            binding.tvDesc.text = data.description
            Glide.with(this@StoryDetailActivity)
                .load(data.photoUrl)
                .placeholder(ColorDrawable(getRandomMaterialColor()))
                .into(binding.ivDetailImg)
        }
    }

    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }

    private fun getRandomMaterialColor(): Int {
        val colors = arrayOf("#EF5350", "#EC407A", "#AB47BC", "#7E57C2", "#5C6BC0",
            "#42A5F5", "#29B6F6", "#26C6DA", "#26A69A", "#66BB6A", "#9CCC65",
            "#D4E157", "#FFEE58", "#FFA726", "#FF7043", "#8D6E63", "#BDBDBD",
            "#78909C")

        return Color.parseColor(colors[Random.nextInt(colors.size)])
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.dropdown_menu, menu)
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
                true
            }
            else -> true
        }
    }

    companion object {
        const val EXTRA_STORY_ITEM = "extra_username"
    }
}