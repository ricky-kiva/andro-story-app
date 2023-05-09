package com.rickyslash.storyapp.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rickyslash.storyapp.api.ApiService
import com.rickyslash.storyapp.api.response.AllStoriesResponse
import com.rickyslash.storyapp.api.response.ListStoryItem
import com.rickyslash.storyapp.ui.main.MainViewModel
import com.rickyslash.storyapp.ui.main.StoriesAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryPagingSource(private val apiService: ApiService): PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStoriesForPaging(position, params.loadSize, 0).listStory

            LoadResult.Page(
                responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else (position - 1),
                nextKey = if (responseData.isNullOrEmpty()) null else (position + 1)
            )

        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

}