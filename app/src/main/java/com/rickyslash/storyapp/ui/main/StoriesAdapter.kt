package com.rickyslash.storyapp.ui.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rickyslash.storyapp.R
import com.rickyslash.storyapp.api.response.ListStoryItem
import com.rickyslash.storyapp.databinding.ItemStoryBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

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

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }
}