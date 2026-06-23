package com.example.sedo.ui.etc

import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import com.example.sedo.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // 뒤로가기 버튼
        val toolbar = findViewById<Toolbar>(R.id.toolbar_settings)
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert)
        toolbar.setNavigationOnClickListener { finish() }

        // Intent 수신
        val receivedUserName = intent.getStringExtra("USER_NAME") ?: "SEDO 유저"
        val tvUsername = findViewById<TextView>(R.id.tv_settings_username)
        tvUsername.text = receivedUserName

        // 미구현 기능 처리
        val switchNotifications = findViewById<SwitchCompat>(R.id.switch_notifications)
        val switchDarkMode = findViewById<SwitchCompat>(R.id.switch_dark_mode)

        // 알림 스위치
        switchNotifications.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) { // 사용자가 끄려고 시도할 때
                Toast.makeText(this, "추후 구현 예정입니다!", Toast.LENGTH_SHORT).show()
                buttonView.isChecked = true // 강제로 다시 켬
            }
        }

        // 다크모드 스위치
        switchDarkMode.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) { // 사용자가 켜려고 시도할 때
                Toast.makeText(this, "추후 구현 예정입니다!", Toast.LENGTH_SHORT).show()
                buttonView.isChecked = false // 강제로 다시 끔
            }
        }

        // 앱 정보 화면으로 이동
        val layoutAppInfo = findViewById<RelativeLayout>(R.id.layout_go_to_info)
        layoutAppInfo.setOnClickListener {
            val infoIntent = Intent(this, AppInfoActivity::class.java).apply {
                putExtra("APP_VERSION", "v1.0.1 (Release)")
                putExtra("BUILD_DATE", "2026-06-21")
            }
            startActivity(infoIntent)
        }
    }
}