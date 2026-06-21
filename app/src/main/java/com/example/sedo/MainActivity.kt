package com.example.sedo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.sedo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.navView.setupWithNavController(navController)

        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_settings -> {
                    val intent = Intent(this, com.example.sedo.ui.etc.SettingsActivity::class.java).apply {
                        putExtra("USER_NAME", "소설실 마스터")
                    }
                    startActivity(intent)

                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_info -> {
                    val intent = Intent(this, com.example.sedo.ui.etc.AppInfoActivity::class.java).apply {
                        putExtra("APP_VERSION", "v1.0.1 (Release)")
                        putExtra("BUILD_DATE", "2026-06-22")
                    }
                    startActivity(intent)

                    binding.drawerLayout.closeDrawers()
                    true
                }
                else -> {
                    val handled = NavigationUI.onNavDestinationSelected(item, navController)
                    if (handled) {
                        binding.drawerLayout.closeDrawers()
                    }
                    handled
                }
            }
        }
    }
}