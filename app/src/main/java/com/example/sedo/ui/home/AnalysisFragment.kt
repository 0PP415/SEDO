package com.example.sedo.ui.home

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sedo.R
import com.example.sedo.BuildConfig
import com.example.sedo.databinding.FragmentAnalysisBinding
import com.example.sedo.ui.home.ml.SymbolDetector
import com.example.sedo.ui.home.ml.TextExtractor
import com.example.sedo.ui.home.ml.GeminiAdvisor
import com.example.sedo.ui.home.youtube.YouTubeClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnalysisFragment : Fragment(R.layout.fragment_analysis) {

    private var _binding: FragmentAnalysisBinding? = null
    private val binding get() = _binding!!

    private lateinit var symbolDetector: SymbolDetector
    private lateinit var textExtractor: TextExtractor
    private lateinit var geminiAdvisor: GeminiAdvisor

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAnalysisBinding.bind(view)

        symbolDetector = SymbolDetector(requireContext())
        textExtractor = TextExtractor()
        geminiAdvisor = GeminiAdvisor()

        val imageUriString = arguments?.getString("imageUri")
        if (imageUriString != null) {
            val uri = Uri.parse(imageUriString)
            startAiPipeline(uri)
        } else {
            findNavController().popBackStack()
        }
    }

    private fun startAiPipeline(uri: Uri) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val bitmap = uriToBitmap(uri)

                if (bitmap != null) {
                    val detectedSymbols = symbolDetector.detectSymbols(bitmap)
                    val extractedText = textExtractor.extractText(bitmap)

                    val finalGuide = geminiAdvisor.generateWashGuide(detectedSymbols, extractedText)
                    Log.d("SEDO_AI", "최종 AI 가이드:\n$finalGuide")

                    var video1Id: String? = "oWSEHGV_IMc"
                    var video2Id: String? = "4LLnoEotPoI"
                    var video1Title: String? = "아끼는 옷 오래 입자! 소재별 옷관리, 세탁 꿀팁까지 총정리!"
                    var video2Title: String? = "드럼 세탁기 돌리는 방법 / 드럼세탁시 유의사항 / 드럼세탁기사용법"

                    try {
                        val keywordSection = finalGuide.substringAfter("[검색 키워드]", "").replace(":", "").trim()
                        val keywords = keywordSection.split(",").map { it.trim() }

                        val kw1 = keywords.getOrNull(0)?.takeIf { it.isNotBlank() } ?: "옷 세탁법"
                        val kw2 = keywords.getOrNull(1)?.takeIf { it.isNotBlank() } ?: "세탁 꿀팁"

                        val youtubeKey = BuildConfig.YOUTUBE_API_KEY
                        val res1 = YouTubeClient.service.searchVideos(query = kw1, apiKey = youtubeKey).items.firstOrNull()
                        val res2 = YouTubeClient.service.searchVideos(query = kw2, apiKey = youtubeKey).items.firstOrNull()

                        res1?.let {
                            video1Id = it.id.videoId ?: video1Id
                            video1Title = it.snippet.title
                        }

                        res2?.let {
                            video2Id = it.id.videoId ?: video2Id
                            video2Title = it.snippet.title
                        }

                        Log.d("SEDO_YOUTUBE", "1번 키워드($kw1) 결과: $video1Title")
                        Log.d("SEDO_YOUTUBE", "2번 키워드($kw2) 결과: $video2Title")

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("SEDO_AI", "유튜브 통신/파싱 에러")
                    }

                    withContext(Dispatchers.Main) {
                        val bundle = Bundle().apply {
                            putString("imageUri", uri.toString())
                            putStringArrayList("detectedSymbols", ArrayList(detectedSymbols.map { it.label }))

                            val displayGuide = finalGuide.substringBefore("[검색 키워드]").trim()
                            putString("aiWashGuide", displayGuide)

                            putString("video1Id", video1Id)
                            putString("video2Id", video2Id)
                            putString("video1Title", video1Title)
                            putString("video2Title", video2Title)
                        }
                        findNavController().navigate(R.id.resultFragment, bundle)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ -> decoder.isMutableRequired = true }
            } else {
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        symbolDetector.close()
        _binding = null
    }
}