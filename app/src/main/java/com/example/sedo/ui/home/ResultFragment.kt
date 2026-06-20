package com.example.sedo.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sedo.R
import com.example.sedo.databinding.FragmentResultBinding
import com.example.sedo.databinding.ItemYoutubeVideoBinding
import com.example.sedo.ui.home.ml.SymbolMapper
import com.google.android.flexbox.FlexboxLayout

data class YoutubeItem(val title: String, val videoId: String)

class YoutubeAdapter(
    private val items: List<YoutubeItem>,
    private val onVideoClick: (String) -> Unit
) : RecyclerView.Adapter<YoutubeAdapter.YoutubeViewHolder>() {

    inner class YoutubeViewHolder(private val binding: ItemYoutubeVideoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: YoutubeItem) {
            binding.tvVideoTitle.text = item.title

            val thumbnailUrl = "https://img.youtube.com/vi/${item.videoId}/hqdefault.jpg"
            Glide.with(binding.root.context)
                .load(thumbnailUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.ivThumbnail)

            binding.root.setOnClickListener { onVideoClick(item.videoId) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YoutubeViewHolder {
        val binding = ItemYoutubeVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return YoutubeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: YoutubeViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size
}

class ResultFragment : Fragment(R.layout.fragment_result) {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentResultBinding.bind(view)

        val imageUriString = arguments?.getString("imageUri")
        val aiWashGuide = arguments?.getString("aiWashGuide") ?: ""
        val detectedSymbols = arguments?.getStringArrayList("detectedSymbols") ?: emptyList()

        val video1Id = arguments?.getString("video1Id") ?: "oM1d82x1K2E"
        val video2Id = arguments?.getString("video2Id") ?: "hQe_f-kXYRk"

        // ⭐️ 하드코딩 제거! AnalysisFragment에서 보내준 진짜 제목을 꺼냅니다.
        val video1Title = arguments?.getString("video1Title") ?: "의류 소재별 세탁 및 목 관리법"
        val video2Title = arguments?.getString("video2Title") ?: "옷 오래 입는 세탁 주의사항"

        imageUriString?.let { uri ->
            Glide.with(this).load(uri).into(binding.ivResultImage)
        }

        val cleanGuide = aiWashGuide.substringBefore("[검색 키워드]").trim()
        binding.tvAiGuide.text = cleanGuide

        setupDynamicBadges(detectedSymbols)

        setupButtons(imageUriString, cleanGuide, detectedSymbols, video1Id, video2Id, video1Title, video2Title)
        setupYoutubeList(video1Id, video2Id, video1Title, video2Title)
    }

    private fun setupDynamicBadges(symbols: List<String>) {
        binding.flexboxSymbols.removeAllViews()
        symbols.forEach { label ->
            val koreanName = SymbolMapper.getKoreanName(label)
            val isWarning = SymbolMapper.isWarning(label)

            val badge = TextView(requireContext()).apply {
                text = if (isWarning) "🚨 $koreanName" else "✓ $koreanName"
                textSize = 12f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setPadding(28, 14, 28, 14)
                layoutParams = FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 16, 16) }

                if (isWarning) {
                    setTextColor(android.graphics.Color.parseColor("#D93025"))
                    setBackgroundResource(R.drawable.bg_badge_warning)
                } else {
                    setTextColor(android.graphics.Color.parseColor("#1A73E8"))
                    setBackgroundResource(R.drawable.bg_badge_info)
                }
            }
            binding.flexboxSymbols.addView(badge)
        }
    }

    private fun setupButtons(
        imageUriString: String?, washGuide: String, detectedSymbols: List<String>,
        v1Id: String, v2Id: String, v1Title: String, v2Title: String
    ) {
        binding.toolbarResult.setNavigationOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }

        binding.btnSaveCloset.setOnClickListener {
            imageUriString?.let { uri ->
                val bundle = Bundle().apply {
                    putString("imageUri", uri)
                    putString("washGuide", washGuide)
                    putStringArrayList("tagSymbols", ArrayList(detectedSymbols))
                    putString("video1Id", v1Id)
                    putString("video2Id", v2Id)
                    putString("video1Title", v1Title)
                    putString("video2Title", v2Title)
                }
                findNavController().navigate(R.id.saveFormFragment, bundle)
            }
        }
    }

    private fun setupYoutubeList(v1: String, v2: String, t1: String, t2: String) {
        val mockVideos = listOf(YoutubeItem(t1, v1), YoutubeItem(t2, v2))
        binding.rvYoutubeRecs.adapter = YoutubeAdapter(mockVideos) { videoId ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
            try { startActivity(intent) } catch (e: Exception) { e.printStackTrace() }
        }
        binding.rvYoutubeRecs.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}