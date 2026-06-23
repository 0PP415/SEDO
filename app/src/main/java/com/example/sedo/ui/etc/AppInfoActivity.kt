package com.example.sedo.ui.etc

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.sedo.R

class AppInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_info)

        // 툴바 세팅
        val toolbar = findViewById<Toolbar>(R.id.toolbar_app_info)
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert)
        toolbar.setNavigationOnClickListener { finish() }

        // SettingsActivity에서 보낸 Intent 데이터 수신
        val appVersion = intent.getStringExtra("APP_VERSION") ?: "v1.0.0"
        val buildDate = intent.getStringExtra("BUILD_DATE") ?: "2026-06-20"

        //  수신한 데이터 바인딩
        val tvVersionInfo = findViewById<TextView>(R.id.tv_version_info)
        val tvBuildDate = findViewById<TextView>(R.id.tv_build_date)

        tvVersionInfo.text = "최신 버전 $appVersion"
        tvBuildDate.text = buildDate
    }
}