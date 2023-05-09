package com.rickyslash.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.rickyslash.storyapp.api.ApiService
import com.rickyslash.storyapp.api.response.ListStoryItem
import com.rickyslash.storyapp.database.StoryDatabase

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService) {
    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}