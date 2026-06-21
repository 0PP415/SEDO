package com.example.sedo.ui.etc

import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.sedo.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // 툴바 세팅 (뒤로가기 버튼)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_settings)
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert) // 기본 뒤로가기 화살표
        toolbar.setNavigationOnClickListener { finish() }

        // Intent 수신
        val receivedUserName = intent.getStringExtra("USER_NAME") ?: "SEDO 유저"
        val tvUsername = findViewById<TextView>(R.id.tv_settings_username)
        tvUsername.text = receivedUserName

        // Intent 송신
        val layoutAppInfo = findViewById<RelativeLayout>(R.id.layout_go_to_info)
        layoutAppInfo.setOnClickListener {
            // Intent를 활용한 데이터 전달
            val infoIntent = Intent(this, AppInfoActivity::class.java).apply {
                putExtra("APP_VERSION", "v1.0.1 (Release)")
                putExtra("BUILD_DATE", "2026-06-21")
            }
            startActivity(infoIntent)
        }
    }
}