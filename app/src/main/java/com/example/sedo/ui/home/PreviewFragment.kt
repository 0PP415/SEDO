package com.example.sedo.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide // ⭐️ Glide 임포트
import com.example.sedo.R
import com.example.sedo.databinding.FragmentPreviewBinding

class PreviewFragment : Fragment(R.layout.fragment_preview) {

    private var _binding: FragmentPreviewBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: String? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            currentImageUri = uri.toString()
            // ⭐️ Glide 적용
            Glide.with(this)
                .load(uri)
                .into(binding.ivPreviewFull)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPreviewBinding.bind(view)

        currentImageUri = arguments?.getString("imageUri")

        if (currentImageUri != null) {
            // ⭐️ Glide 적용
            Glide.with(this)
                .load(currentImageUri)
                .into(binding.ivPreviewFull)
        } else {
            Toast.makeText(requireContext(), "사진을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        setupButtons()
    }

    private fun setupButtons() {
        binding.toolbarPreview.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnRetake.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnAnalyze.setOnClickListener {
            // let을 사용하면 null이 아닐 때만 안전하게 안쪽 코드가 실행됩니다. (코틀린스러운 문법!)
            currentImageUri?.let { uri ->
                val bundle = Bundle().apply { putString("imageUri", uri) }
                findNavController().navigate(R.id.analysisFragment, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}