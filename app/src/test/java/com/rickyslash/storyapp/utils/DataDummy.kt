package com.rickyslash.storyapp.utils

import com.rickyslash.storyapp.api.response.ListStoryItem

object DataDummy {
    fun generateDummyQuoteResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "id $i",
                "photo $i",
                "createdAt $i",
                "name $i",
                "desc $i",
                i.toDouble(),
                i.toDouble()

            )
            items.add(story)
        }
        return items
    }
}