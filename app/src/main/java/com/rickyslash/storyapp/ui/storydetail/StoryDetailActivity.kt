package com.rickyslash.storyapp.ui.storydetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rickyslash.storyapp.R

class StoryDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_detail)
    }

    companion object {
        const val EXTRA_STORY_ITEM = "extra_username"
    }
}