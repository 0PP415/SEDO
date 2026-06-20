package com.example.sedo.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // ⭐️ Glide 임포트
import com.example.sedo.data.ClothEntity
import com.example.sedo.databinding.ItemRecentClothBinding

class RecentClothesAdapter(
    private var items: List<ClothEntity>,
    private val onItemClick: (ClothEntity) -> Unit // ⭐️ 클릭 이벤트 콜백 추가!
) : RecyclerView.Adapter<RecentClothesAdapter.RecentViewHolder>() {

    inner class RecentViewHolder(private val binding: ItemRecentClothBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ClothEntity) {

            // ⭐️ Glide 적용: 썸네일 크기로 알아서 최적화해서 넣어줍니다.
            Glide.with(binding.root.context)
                .load(item.imageUri)
                .centerCrop()
                .into(binding.ivRecentImage)

            binding.tvRecentCategory.text = "${item.category} • ${item.season}"
            binding.tvRecentName.text = item.name

            // ⭐️ 카드 클릭 시 상세 페이지로 이동하도록 밖으로 데이터 던지기
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