package com.example.sedo.ui.closet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.sedo.R
import com.example.sedo.data.ClothEntity
import com.example.sedo.databinding.FragmentDetailBinding
import com.example.sedo.ui.ClosetViewModel
import com.example.sedo.ui.home.YoutubeAdapter
import com.example.sedo.ui.home.YoutubeItem
import com.example.sedo.ui.home.ml.SymbolMapper
import com.google.android.flexbox.FlexboxLayout

class DetailFragment : Fragment(R.layout.fragment_detail) {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ClosetViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailBinding.bind(view)
        viewModel = ViewModelProvider(this)[ClosetViewModel::class.java]

        // 의류 데이터 수신
        val clothId = arguments?.getLong("id", 0L) ?: 0L
        val name = arguments?.getString("name") ?: ""
        val imageUri = arguments?.getString("imageUri") ?: ""
        val category = arguments?.getString("category") ?: ""
        val season = arguments?.getString("season") ?: ""
        val washGuide = arguments?.getString("washGuide") ?: ""
        val tagSymbols = arguments?.getStringArrayList("tagSymbols") ?: emptyList()

        // 유튜브 데이터 추출
        val rawV1 = arguments?.getString("video1Id")?.trim()
        val video1Id = if (rawV1.isNullOrEmpty() || rawV1 == "null") "V4JgQ2Q6z64" else rawV1

        val rawV2 = arguments?.getString("video2Id")?.trim()
        val video2Id = if (rawV2.isNullOrEmpty() || rawV2 == "null") "hQe_f-kXYRk" else rawV2

        val rawT1 = arguments?.getString("video1Title")?.trim()
        val video1Title = if (rawT1.isNullOrEmpty() || rawT1 == "null") "기본 세탁 가이드 1" else rawT1

        val rawT2 = arguments?.getString("video2Title")?.trim()
        val video2Title = if (rawT2.isNullOrEmpty() || rawT2 == "null") "기본 세탁 가이드 2" else rawT2

        // 뷰 데이터 바인딩
        binding.tvDetailName.text = name
        binding.tvBadgeCategory.text = category
        binding.tvBadgeSeason.text = season
        binding.tvDetailWashGuide.text = washGuide

        imageUri?.let { uri ->
            Glide.with(this).load(uri).centerCrop().into(binding.ivDetailImage)
        }

        binding.toolbarDetail.setNavigationOnClickListener { findNavController().popBackStack() }

        // 세탁 기호 뱃지 생성
        val displayTags = tagSymbols.ifEmpty { listOf("ER_no_tumble_dry", "ER_wash_40") }
        setupDynamicBadges(displayTags)

        // 버튼 및 리사이클러뷰 세팅
        setupButtons(clothId, name, imageUri, category, season, washGuide, tagSymbols, video1Id, video2Id, video1Title, video2Title)
        setupYoutubeList(video1Id, video2Id, video1Title, video2Title)
    }

    private fun setupDynamicBadges(symbols: List<String>) {
        binding.flexboxDetailSymbols.removeAllViews()
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
            binding.flexboxDetailSymbols.addView(badge)
        }
    }

    private fun setupButtons(
        clothId: Long, name: String?, imageUri: String?, category: String?, season: String?, washGuide: String?,
        tagSymbols: List<String>, video1Id: String, video2Id: String, video1Title: String, video2Title: String
    ) {
        binding.btnDeleteCloth.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("옷 삭제")
                .setMessage("정말로 이 옷을 삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    val clothToDelete = ClothEntity(
                        id = clothId, imageUri = imageUri ?: "", name = name ?: "", category = category ?: "", season = season ?: "",
                        material = "", washGuide = washGuide ?: "", tagSymbols = tagSymbols,
                        video1Id = video1Id, video2Id = video2Id, video1Title = video1Title, video2Title = video2Title
                    )
                    viewModel.deleteCloth(clothToDelete)
                    Toast.makeText(requireContext(), "옷장에서 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                .setNegativeButton("취소", null)
                .show()
        }

        binding.btnEditCloth.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean("isEditMode", true)
                putLong("id", clothId)
                putString("imageUri", imageUri)
                putString("name", name)
                putString("category", category)
                putString("season", season)
                putString("washGuide", washGuide)
                putStringArrayList("tagSymbols", ArrayList(tagSymbols))
                putString("video1Id", video1Id)
                putString("video2Id", video2Id)
                putString("video1Title", video1Title)
                putString("video2Title", video2Title)
            }
            findNavController().navigate(R.id.saveFormFragment, bundle)
        }
    }

    private fun setupYoutubeList(v1: String, v2: String, t1: String, t2: String) {
        val detailVideos = listOf(YoutubeItem(t1, v1), YoutubeItem(t2, v2))

        binding.rvDetailYoutube.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvDetailYoutube.adapter = YoutubeAdapter(detailVideos) { videoId ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
            try { startActivity(intent) } catch (e: Exception) { e.printStackTrace() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}