package com.rickyslash.storyapp.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rickyslash.storyapp.R
import com.rickyslash.storyapp.api.response.ListStoryItem
import com.rickyslash.storyapp.databinding.ActivityMainBinding
import com.rickyslash.storyapp.helper.ViewModelFactory
import com.rickyslash.storyapp.model.UserPreference
import com.rickyslash.storyapp.ui.addstory.AddStoryActivity
import com.rickyslash.storyapp.ui.login.LoginActivity
import com.rickyslash.storyapp.ui.storydetail.StoryDetailActivity
import com.rickyslash.storyapp.helper.titleSentence

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

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
        mainViewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(dataStore)))[MainViewModel::class.java]
        mainViewModel.getUser().observe(this) { user ->
            if (!user.isLogin) {
                intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                binding.tvGreetName.text = getString(R.string.greet_name, titleSentence(user.name))
                setupUserLoggedIn(user.token)
            }
        }
        observeLoading()
    }

    private fun setupAction() {
        setupRV()
    }

    private fun setupUserLoggedIn(token: String) {
        mainViewModel.getStories(token)
        isErrorObserver = Observer { isError ->
            if (!isError) {
                mainViewModel.listStoryItem.observe(this) {
                    setStoriesData(it)
                }
            } else {
                responseMessageObserver = Observer { responseMessage ->
                    if (responseMessage != null) {
                        Toast.makeText(this, responseMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                responseMessageObserver?.let {
                    mainViewModel.responseMessage.observeOnce(this, it)
                }
            }
        }
        isErrorObserver?.let {
            mainViewModel.isError.observe(this, it)
        }
    }

    private fun setupRV() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager
    }

    private fun setStoriesData(storiesData: List<ListStoryItem>) {
        val storiesAdapter = StoriesAdapter(storiesData)
        binding.rvStories.adapter = storiesAdapter

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
                Toast.makeText(this@MainActivity, "\uD83D\uDC68\u200D\uD83D\uDCBB rickyslash.my.id", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_logout -> {
                mainViewModel.logout()
                true
            }
            R.id.add_story -> {
                startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
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

    private fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
        observe(owner, object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value)
                removeObserver(this)
            }
        })
    }

}