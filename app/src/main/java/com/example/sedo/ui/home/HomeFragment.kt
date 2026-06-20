package com.example.sedo.ui.home

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sedo.R
import com.example.sedo.databinding.FragmentHomeBinding
import com.example.sedo.BuildConfig
import com.example.sedo.ui.ClosetViewModel
import com.example.sedo.ui.home.weather.WeatherClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ClosetViewModel
    private lateinit var recentAdapter: RecentClothesAdapter

    // 1. 갤러리 결과 처리 런처
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            val bundle = Bundle().apply { putString("imageUri", it.toString()) }
            findNavController().navigate(R.id.previewFragment, bundle)
        }
    }

    // 2. 카메라 촬영 결과 처리 런처와 임시 사진 주소 보관용 변수
    private var cameraUri: Uri? = null
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && cameraUri != null) {
            val bundle = Bundle().apply { putString("imageUri", cameraUri.toString()) }
            findNavController().navigate(R.id.previewFragment, bundle)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        viewModel = ViewModelProvider(this)[ClosetViewModel::class.java]

        setupRecyclerView()
        setupToolbarWithNavigation()
        setupClickListeners()
        setupFragmentResultListeners()
        observeRecentClothes()
        checkWeatherCondition()
    }

    // 사진 추가 방식 선택 다이얼로그
    private fun showImageSourceDialog() {
        val options = arrayOf("📸 카메라로 촬영", "🖼️ 갤러리에서 선택")
        AlertDialog.Builder(requireContext())
            .setTitle("옷 사진 추가하기")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    // 갤러리 열기 분리
    private fun openGallery() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    // 권한 없이 카메라를 실행하는 코드 (MediaStore 활용)
    private fun openCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "sedo_cloth_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        cameraUri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        cameraUri?.let { takePicture.launch(it) }
    }

    private fun setupToolbarWithNavigation() {
        val navController = findNavController()
        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.closetFragment, R.id.guideFragment),
            drawerLayout
        )
        binding.toolbarHome.setupWithNavController(navController, appBarConfiguration)
    }

    private fun setupClickListeners() {
        binding.cardScanTag.setOnClickListener {
            showImageSourceDialog()
        }
    }

    private fun setupFragmentResultListeners() {
        // 1. 저장 성공 후 돌아왔을 때 스낵바 띄우기
        setFragmentResultListener("save_request") { _, bundle ->
            // ⭐️ 기존의 isSaved(Boolean) 대신 message(String)를 꺼냅니다!
            val message = bundle.getString("message")
            if (message != null) {
                showSavedSnackBar(message) // 꺼낸 문구를 함수로 전달
            }
        }

        // 2. 옷장에서 + 버튼 누르고 돌아왔을 때 자동 실행 ➡️ 갤러리 직행이 아닌 선택 다이얼로그를 띄우도록 수정!
        setFragmentResultListener("auto_open_gallery") { _, bundle ->
            val autoOpen = bundle.getBoolean("autoOpen", false)
            if (autoOpen) {
                showImageSourceDialog()
            }
        }
    }

    private fun setupRecyclerView() {
        // 어댑터 초기화 및 클릭 시 id를 포함한 번들 포장 연동
        recentAdapter = RecentClothesAdapter(emptyList()) { cloth ->
            val bundle = Bundle().apply {
                putLong("id", cloth.id) // ⭐️ 수정/삭제 작업을 위해 고유 식별 번호 반드시 포함
                putString("name", cloth.name)
                putString("imageUri", cloth.imageUri)
                putString("category", cloth.category)
                putString("season", cloth.season)
                putString("washGuide", cloth.washGuide)
            }
            findNavController().navigate(R.id.detailFragment, bundle)
        }

        binding.rvRecentClothes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvRecentClothes.adapter = recentAdapter
    }

    private fun observeRecentClothes() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allClothes.collect { clothesList ->
                if (clothesList.isEmpty()) {
                    binding.rvRecentClothes.visibility = View.GONE
                    binding.tvEmptyRecent.visibility = View.VISIBLE
                } else {
                    binding.tvEmptyRecent.visibility = View.GONE
                    binding.rvRecentClothes.visibility = View.VISIBLE
                    recentAdapter.updateItems(clothesList.take(5))
                }
            }
        }
    }

    // ⭐️ 실시간 날씨 API 연동 로직으로 전면 수정!
    private fun checkWeatherCondition() {
        val myApiKey = BuildConfig.OPENWEATHER_API_KEY
        val city = "Busan"

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = WeatherClient.service.getCurrentWeather(city, myApiKey)

                val currentTemp = response.main.temp.toInt()
                val currentHumidity = response.main.humidity // ⭐️ 실시간 습도 파싱
                val weatherDescription = response.weatherList.firstOrNull()?.description ?: "맑음"
                val weatherState = response.weatherList.firstOrNull()?.mainState ?: "Clear"

                // ⭐️ 1. 상단 큼직한 온습도 텍스트뷰 반영 ("22°C  |  습도 45%")
                // 보내주신 XML의 ID인 tv_weather_temp와 정확히 매치시킵니다.
                binding.tvWeatherTemp.text = "${currentTemp}°C  |  습도 ${currentHumidity}%"

                // 2. 하단 스마트 세탁 지수 박스 반영
                val laundryIndexText = when (weatherState) {
                    "Clear" -> "☀️ 세탁 지수: 아주 좋음!\n현재 기온은 ${currentTemp}°C이며, 빨래가 뽀송뽀송하게 잘 마르는 날씨예요."
                    "Clouds" -> "☁️ 세탁 지수: 보통\n현재 기온은 ${currentTemp}°C이며, 흐리지만 빨래를 돌리기엔 무난해요."
                    "Rain", "Drizzle", "Thunderstorm" -> "🌧️ 세탁 지수: 나쁨!\n비가 오고 있으니 실내 건조나 건조기 사용을 권장해요."
                    else -> "🌈 현재 날씨: $weatherDescription (${currentTemp}°C)\n오늘도 즐거운 하루 되세요!"
                }
                binding.tvLaundryIndex.text = laundryIndexText

            } catch (e: Exception) {
                e.printStackTrace()
                // 에러 발생 시 UI 예외 처리
                binding.tvWeatherTemp.text = "--°C  |  습도 --%"
                binding.tvLaundryIndex.text = "⚠️ 날씨 정보를 불러오지 못했습니다.\n네트워크 연결 상태를 확인해 주세요."
            }
        }
    }

    // 괄호 안에 message를 받도록 수정!
    private fun showSavedSnackBar(message: String) {
        // 하드코딩 되어있던 문구를 매개변수 message로 교체
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("옷장 가기") {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(
                        R.id.homeFragment,
                        inclusive = false,
                        saveState = true
                    )
                    .setLaunchSingleTop(true)
                    .setRestoreState(true)
                    .build()

                findNavController().navigate(R.id.closetFragment, null, navOptions)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}