package com.rickyslash.storyapp.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rickyslash.storyapp.R
import com.rickyslash.storyapp.api.response.ListStoryItem
import com.rickyslash.storyapp.data.StoryPagingSource
import com.rickyslash.storyapp.databinding.ActivityMainBinding
import com.rickyslash.storyapp.helper.ViewModelFactory
import com.rickyslash.storyapp.helper.titleSentence
import com.rickyslash.storyapp.ui.addstory.AddStoryActivity
import com.rickyslash.storyapp.ui.login.LoginActivity
import com.rickyslash.storyapp.ui.maps.MapsActivity
import com.rickyslash.storyapp.ui.storydetail.StoryDetailActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var isErrorObserver: Observer<Boolean>? = null
    private var responseMessageObserver: Observer<String?>? = null
    private var isLoadingObserver: Observer<Boolean>? = null

    private fun showStoryDetails(data: ListStoryItem) {
        val intent = Intent(this@MainActivity, StoryDetailActivity::class.java)
        intent.putExtra(StoryDetailActivity.EXTRA_STORY_ITEM, data)
        startActivity(intent)
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
            text.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@MainActivity, R.color.black)), 0, text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            elevation = 0f
            setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@MainActivity, R.color.teal_EDC)))
            title = text
            setHomeAsUpIndicator(R.drawable.ic_polaroid_black_24)
            setHomeActionContentDescription(getString(R.string.app_name))
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(this, ViewModelFactory(application))[MainViewModel::class.java]
        observeLoading()
        val user = mainViewModel.getPreferences()
        if (!user.isLogin) {
            intentBackToLogin()
        } else {
            binding.tvGreetName.text = getString(R.string.greet_name, user.name?.let { titleSentence(it) })
            // setupUserLoggedIn()
        }
    }

    private fun setupAction() {
        setupRV()
        setStoriesData()
    }

//    private fun setupUserLoggedIn() {
//        mainViewModel.getStories()
//        isErrorObserver = Observer { isError ->
//            if (!isError) {
//                mainViewModel.listStoryItem.observe(this) {
//                    if (it.isNotEmpty()) {
//                        binding.tvGreetWhatsup.text = getString(R.string.whatsup)
//                    } else {
//                        binding.tvGreetWhatsup.text = getString(R.string.list_story_empty)
//                        Toast.makeText(this, getString(R.string.list_story_empty), Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }
//        responseMessageObserver = Observer { responseMessage ->
//            if (responseMessage != null && mainViewModel.isError.value == true) {
//                if (responseMessage == "Invalid token signature") {
//                    Toast.makeText(this, getString(R.string.warn_token_expired), Toast.LENGTH_SHORT).show()
//                    intentBackToLogin()
//                }
//                Toast.makeText(this, responseMessage, Toast.LENGTH_SHORT).show()
//            }
//        }
//        isErrorObserver?.let { mainViewModel.isError.observe(this, it) }
//        responseMessageObserver?.let { mainViewModel.responseMessage.observe(this, it) }
//    }

    private fun setupRV() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager
    }

    private fun setStoriesData() {
        val storiesAdapter = StoriesAdapter()
        binding.rvStories.adapter = storiesAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storiesAdapter.retry()
            }
        )
        mainViewModel.story.observe(this) {
            storiesAdapter.submitData(lifecycle, it)
        }

        storiesAdapter.setOnItemClickCallback(object : StoriesAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                showStoryDetails(data)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.mainClHeader.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.rvStories.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun observeLoading() {
        isLoadingObserver = Observer { showLoading(it) }
        isLoadingObserver?.let {
            mainViewModel.isLoading.observe(this, it)
        }
    }

    private fun intentBackToLogin() {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
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
                Toast.makeText(this@MainActivity, getString(R.string.dev_easter_web), Toast.LENGTH_SHORT).show()
                true
            }
            R.id.add_story -> {
                startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
                true
            }
            R.id.menu_maps -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                true
            }
            R.id.menu_logout -> {
                mainViewModel.logout()
                if (!mainViewModel.getPreferences().isLogin) {
                    intentBackToLogin()
                }
                true
            }
            R.id.menu_translate -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            else -> true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isErrorObserver?.let(mainViewModel.isError::removeObserver)
        responseMessageObserver?.let(mainViewModel.responseMessage::removeObserver)
        isLoadingObserver?.let(mainViewModel.isLoading::removeObserver)
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

}