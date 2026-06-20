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

    // DB 창고에서 관리인을 호출합니다.
    private val clothDao = AppDatabase.getDatabase(application).clothDao()

    // 창고의 모든 옷 데이터를 실시간으로 감시하는 통로(StateFlow)입니다.
    // 데이터가 추가되거나 삭제되면 이 통로를 바라보는 화면이 알아서 자동 새로고침 됩니다!
    val allClothes: StateFlow<List<ClothEntity>> = clothDao.getAllClothes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // [C] 기존에 있던 저장 기능
    fun insertCloth(cloth: ClothEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            clothDao.insertCloth(cloth) // (또는 dao.insertCloth(cloth) 형태일 수 있습니다)
        }
    }

    // [U] 새로 추가할 수정 기능
    fun updateCloth(cloth: ClothEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            // 본인의 프로젝트 구조에 맞춰 repository.updateCloth 나 dao.updateCloth 로 적어주세요!
            clothDao.updateCloth(cloth)
        }
    }

    // [D] 새로 추가할 삭제 기능
    fun deleteCloth(cloth: ClothEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            clothDao.deleteCloth(cloth)
        }
    }
}