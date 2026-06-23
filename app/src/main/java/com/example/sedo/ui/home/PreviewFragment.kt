package com.example.sedo.ui.home

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.sedo.R
import com.example.sedo.databinding.FragmentPreviewBinding

class PreviewFragment : Fragment(R.layout.fragment_preview) {

    private var _binding: FragmentPreviewBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: String? = null

    // 갤러리 런처
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            currentImageUri = uri.toString()
            Glide.with(this)
                .load(uri)
                .into(binding.ivPreviewFull)
        }
    }

    // 카메라 런처 및 임시 URI 변수 추가
    private var cameraUri: Uri? = null
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && cameraUri != null) {
            currentImageUri = cameraUri.toString()
            Glide.with(this)
                .load(cameraUri)
                .into(binding.ivPreviewFull)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPreviewBinding.bind(view)

        currentImageUri = arguments?.getString("imageUri")

        if (currentImageUri != null) {
            Glide.with(this)
                .load(currentImageUri)
                .into(binding.ivPreviewFull)
        } else {
            Toast.makeText(requireContext(), "사진을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        setupButtons()
    }

    // 다이얼로그 및 열기 함수
    private fun showImageSourceDialog() {
        val options = arrayOf("카메라로 다시 촬영", "갤러리에서 다시 선택")
        AlertDialog.Builder(requireContext())
            .setTitle("사진 변경하기")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun openGallery() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun openCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "sedo_cloth_retake_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        cameraUri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        cameraUri?.let { takePicture.launch(it) }
    }

    private fun setupButtons() {
        binding.toolbarPreview.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnRetake.setOnClickListener {
            showImageSourceDialog()
        }

        binding.btnAnalyze.setOnClickListener {
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