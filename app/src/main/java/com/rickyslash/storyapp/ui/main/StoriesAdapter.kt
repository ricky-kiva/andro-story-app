package com.rickyslash.storyapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rickyslash.storyapp.api.response.ListStoryItem
import com.rickyslash.storyapp.databinding.ItemStoryBinding
import java.text.SimpleDateFormat
import java.util.*

class StoriesAdapter(private val storyList: List<ListStoryItem>): RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: ItemStoryBinding): RecyclerView.ViewHolder(binding.root)

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = storyList[position]
        holder.binding.storyDetailName.text = data.name
        holder.binding.storyDetailDate.text = formatDate(data.createdAt)
        Glide.with(holder.itemView.context)
            .load(data.photoUrl)
            .into(holder.binding.ivStory)

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(storyList[position]) }
    }

    override fun getItemCount(): Int = storyList.size

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

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }
}