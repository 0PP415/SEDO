package com.example.sedo.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // ⭐️ Glide 임포트
import com.example.sedo.data.ClothEntity
import com.example.sedo.databinding.ItemRecentClothBinding

class RecentClothesAdapter(
    private var items: List<ClothEntity>,
    private val onItemClick: (ClothEntity) -> Unit
) : RecyclerView.Adapter<RecentClothesAdapter.RecentViewHolder>() {

    inner class RecentViewHolder(private val binding: ItemRecentClothBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ClothEntity) {

            Glide.with(binding.root.context)
                .load(item.imageUri)
                .centerCrop()
                .into(binding.ivRecentImage)

            binding.tvRecentCategory.text = "${item.category} • ${item.season}"
            binding.tvRecentName.text = item.name

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    fun updateItems(newItems: List<ClothEntity>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val binding = ItemRecentClothBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}