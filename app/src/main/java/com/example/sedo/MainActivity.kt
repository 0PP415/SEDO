package com.example.sedo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.sedo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. 뷰 바인딩 초기화 및 화면 표시
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. activity_main에 박아둔 네비게이션 호스트(액자)에서 컨트롤러(지도 조종사)를 꺼냅니다
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // 3. ⭐️ 핵심: 왼쪽 서랍장(navView)에 지도 조종사(navController)를 묶어줍니다
        // 이제 메뉴를 누르면 ID를 비교해서 알아서 화면을 전환합니다!
        binding.navView.setupWithNavController(navController)
    }
}