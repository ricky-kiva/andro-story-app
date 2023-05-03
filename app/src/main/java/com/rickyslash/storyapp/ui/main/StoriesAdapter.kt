package com.rickyslash.storyapp.ui.main

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rickyslash.storyapp.api.response.ListStoryItem
import com.rickyslash.storyapp.databinding.ItemStoryBinding
import com.rickyslash.storyapp.helper.formatDate
import com.rickyslash.storyapp.helper.getRandomMaterialColor

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
        holder.binding.tvName.text = data.name
        holder.binding.tvDate.text = formatDate(data.createdAt)
        holder.binding.tvDesc.text = data.description
        Glide.with(holder.itemView.context)
            .load(data.photoUrl)
            .placeholder(ColorDrawable(getRandomMaterialColor()))
            .into(holder.binding.ivStory)

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(storyList[position]) }
    }

    override fun getItemCount(): Int = storyList.size

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }
}