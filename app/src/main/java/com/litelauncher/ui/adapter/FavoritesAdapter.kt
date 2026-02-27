package com.litelauncher.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.litelauncher.databinding.ItemFavoriteBinding
import com.litelauncher.model.FavoriteSlot

class FavoritesAdapter(
    private val onClick: (FavoriteSlot) -> Unit,
    private val onLongClick: (FavoriteSlot) -> Unit
) : ListAdapter<FavoriteSlot, FavoritesAdapter.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<FavoriteSlot>() {
            override fun areItemsTheSame(a: FavoriteSlot, b: FavoriteSlot) = a.index == b.index
            override fun areContentsTheSame(a: FavoriteSlot, b: FavoriteSlot) = a == b
        }
    }

    class ViewHolder(val binding: ItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val slot = getItem(position)
        holder.binding.favoriteLabel.text = slot.appInfo?.label ?: "+"
        holder.binding.favoriteLabel.alpha = if (slot.appInfo != null) 1.0f else 0.5f

        holder.binding.root.setOnClickListener { onClick(slot) }
        holder.binding.root.setOnLongClickListener {
            onLongClick(slot)
            true
        }
    }
}
