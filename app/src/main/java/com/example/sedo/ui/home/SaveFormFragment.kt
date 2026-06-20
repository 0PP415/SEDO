package com.example.sedo.ui.home // ⭐️ 유저님의 실제 패키지 경로에 맞게 확인하세요 (ui.home 또는 ui.closet)

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.sedo.R
import com.example.sedo.data.ClothEntity
import com.example.sedo.databinding.FragmentSaveFormBinding
import com.example.sedo.ui.ClosetViewModel
import com.example.sedo.ui.home.ml.SymbolMapper
import com.google.android.flexbox.FlexboxLayout

class SaveFormFragment : Fragment(R.layout.fragment_save_form) {

    private var _binding: FragmentSaveFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ClosetViewModel

    private var isEditMode = false
    private var clothId: Long = 0
    private var imageUri: String = ""
    private var tagSymbols: List<String> = emptyList()

    // ⭐️ 데이터베이스로 보낼 유튜브 전역 데이터 저장소 준비
    private var video1Id: String = ""
    private var video2Id: String = ""
    private var video1Title: String = ""
    private var video2Title: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSaveFormBinding.bind(view)
        viewModel = ViewModelProvider(this)[ClosetViewModel::class.java]

        isEditMode = arguments?.getBoolean("isEditMode") ?: false
        clothId = arguments?.getLong("id") ?: 0L
        imageUri = arguments?.getString("imageUri") ?: ""
        val washGuide = arguments?.getString("washGuide") ?: ""
        tagSymbols = arguments?.getStringArrayList("tagSymbols")?.toList() ?: emptyList()
        val name = arguments?.getString("name") ?: ""
        val category = arguments?.getString("category") ?: ""
        val season = arguments?.getString("season") ?: ""

        // ⭐️ ResultFragment에서 넘어온 유튜브 데이터 안전하게 포획 (공백 차단)
        val v1 = arguments?.getString("video1Id")
        video1Id = if (v1.isNullOrBlank()) "oM1d82x1K2E" else v1
        val v2 = arguments?.getString("video2Id")
        video2Id = if (v2.isNullOrBlank()) "hQe_f-kXYRk" else v2

        video1Title = arguments?.getString("video1Title") ?: "의류 소재별 세탁 및 목 관리법"
        video2Title = arguments?.getString("video2Title") ?: "옷 오래 입는 세탁 주의사항"

        if (imageUri.isNotEmpty()) {
            Glide.with(this).load(imageUri).into(binding.ivFormImage)
        }
        binding.etClothName.setText(name)
        binding.etWashMemo.setText(washGuide)

        setupDynamicBadges(tagSymbols)
        restoreChipSelection(category, season)

        binding.toolbarSaveForm.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.btnCompleteSave.setOnClickListener { saveClothData() }
    }

    private fun setupDynamicBadges(symbols: List<String>) {
        binding.flexboxFormSymbols.removeAllViews()
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
            binding.flexboxFormSymbols.addView(badge)
        }
    }

    private fun restoreChipSelection(category: String, season: String) {
        when (category) {
            "상의" -> binding.chipTop.isChecked = true
            "하의" -> binding.chipBottom.isChecked = true
            "아우터" -> binding.chipOuter.isChecked = true
        }
        when (season) {
            "봄/가을" -> binding.chipSpringFall.isChecked = true
            "여름" -> binding.chipSummer.isChecked = true
            "겨울" -> binding.chipWinter.isChecked = true
        }
    }

    private fun saveClothData() {
        val clothName = binding.etClothName.text.toString().trim()
        val washMemo = binding.etWashMemo.text.toString().trim()

        if (clothName.isEmpty()) {
            Toast.makeText(requireContext(), "옷 이름을 입력해주세요!", Toast.LENGTH_SHORT).show()
            return
        }

        val categoryId = binding.chipGroupCategory.checkedChipId
        val seasonId = binding.chipGroupSeason.checkedChipId

        val category = if (categoryId != View.NO_ID) {
            binding.root.findViewById<com.google.android.material.chip.Chip>(categoryId).text.toString()
        } else "미분류"

        val season = if (seasonId != View.NO_ID) {
            binding.root.findViewById<com.google.android.material.chip.Chip>(seasonId).text.toString()
        } else "사계절"

        // ⭐️ 4개의 고유 데이터 컬럼을 명확하게 꽂아줍니다!
        val clothEntity = ClothEntity(
            id = if (isEditMode) clothId else 0L,
            imageUri = imageUri,
            name = clothName,
            category = category,
            season = season,
            material = "",
            washGuide = washMemo,
            tagSymbols = tagSymbols,
            video1Id = video1Id,
            video2Id = video2Id,
            video1Title = video1Title,
            video2Title = video2Title
        )

        if (isEditMode) viewModel.updateCloth(clothEntity)
        else viewModel.insertCloth(clothEntity)

        Toast.makeText(requireContext(), "저장 완료되었습니다.", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack(R.id.homeFragment, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}