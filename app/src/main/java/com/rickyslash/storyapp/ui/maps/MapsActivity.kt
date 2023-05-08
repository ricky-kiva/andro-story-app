package com.rickyslash.storyapp.ui.maps

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rickyslash.storyapp.R
import com.rickyslash.storyapp.api.response.ListStoryItem
import com.rickyslash.storyapp.databinding.ActivityMapsBinding
import com.rickyslash.storyapp.helper.ViewModelFactory
import com.rickyslash.storyapp.helper.limitString
import com.rickyslash.storyapp.helper.titleSentence
import com.rickyslash.storyapp.ui.addstory.AddStoryActivity
import com.rickyslash.storyapp.ui.login.LoginActivity

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapsViewModel: MapsViewModel
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMapsBinding.inflate(layoutInflater)
    }

    private var isLoadingObserver: Observer<Boolean>? = null
    private var isErrorObserver: Observer<Boolean>? = null
    private var responseMessageObserver: Observer<String?>? = null

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupView()
        setupViewModel()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setupMapData()
        mapUISettings()
    }

    private fun setupView() {
        supportActionBar?.apply {
            val text = SpannableString(supportActionBar?.title)
            text.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@MapsActivity, R.color.black)), 0, text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            elevation = 0f
            setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@MapsActivity, R.color.teal_EDC)))
            title = text
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        }
    }

    private fun setupViewModel() {
        mapsViewModel = ViewModelProvider(this, ViewModelFactory(application))[MapsViewModel::class.java]
        observeLoading()
        val user = mapsViewModel.getPreferences()
        if (!user.isLogin) {
            intentBackToLogin()
        } else {
            binding.tvGreetName.text = getString(R.string.greet_name, user.name?.let { titleSentence(it) })
        }
    }

    private fun setupMapData() {
        mapsViewModel.getStories()
        isErrorObserver = Observer { isError ->
            if (!isError) {
                mapsViewModel.listStoryItem.observe(this) {
                    if (it.isNotEmpty()) {
                        binding.tvGreetWhatsup.text = getString(R.string.label_greet_maps)
                        setMarkerData(it)
                    } else {
                        binding.tvGreetWhatsup.text = getString(R.string.list_story_empty)
                        Toast.makeText(this, getString(R.string.list_story_empty), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        responseMessageObserver = Observer { responseMessage ->
            if (responseMessage != null && mapsViewModel.isError.value == true) {
                Toast.makeText(this, responseMessage, Toast.LENGTH_SHORT).show()
            }
        }
        isErrorObserver?.let { mapsViewModel.isError.observe(this, it) }
        responseMessageObserver?.let { mapsViewModel.responseMessage.observe(this, it) }
    }

    private fun setMarkerData(data: List<ListStoryItem>) {
        data.forEach {
            val latLng = LatLng(it.lat, it.lon)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(it.name)
                    .snippet(limitString(it.description, 30))
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.mapsClHeader.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.map.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun observeLoading() {
        isLoadingObserver = Observer { showLoading(it) }
        isLoadingObserver?.let {
            mapsViewModel.isLoading.observe(this, it)
        }
    }

    private fun mapUISettings() {
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }

    private fun intentBackToLogin() {
        val intent = Intent(this@MapsActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.maps_activity_menu, menu)
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
            R.id.add_story -> {
                startActivity(Intent(this@MapsActivity, AddStoryActivity::class.java))
                true
            }
            R.id.menu_logout -> {
                mapsViewModel.logout()
                if (!mapsViewModel.getPreferences().isLogin) {
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
        isLoadingObserver?.let(mapsViewModel.isLoading::removeObserver)
    }

}