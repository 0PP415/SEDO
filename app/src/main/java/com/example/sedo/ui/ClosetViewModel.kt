package com.example.sedo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sedo.data.AppDatabase
import com.example.sedo.data.ClothEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClosetViewModel(application: Application) : AndroidViewModel(application) {

    private val clothDao = AppDatabase.getDatabase(application).clothDao()

    val allClothes: StateFlow<List<ClothEntity>> = clothDao.getAllClothes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Create
    fun insertCloth(cloth: ClothEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            clothDao.insertCloth(cloth)
        }
    }

    // Update
    fun updateCloth(cloth: ClothEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            // 본인의 프로젝트 구조에 맞춰 repository.updateCloth 나 dao.updateCloth 로 적어주세요!
            clothDao.updateCloth(cloth)
        }
    }

    // Delete
    fun deleteCloth(cloth: ClothEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            clothDao.deleteCloth(cloth)
        }
    }
}