package com.example.sedo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothes_table") // 데이터베이스 안의 표(Table) 이름
data class ClothEntity(
    @PrimaryKey(autoGenerate = true) // 데이터가 추가될 때마다 id를 1, 2, 3... 자동으로 부여
    val id: Long = 0,
    val imageUri: String,            // 갤러리에서 가져온 사진 주소
    val name: String,                // 옷 이름 (예: "애착 흰 티")
    val category: String,            // 카테고리 (예: "상의")
    val season: String,              // 계절
    val material: String,            // 소재
    val washGuide: String,           // AI 세탁 가이드 요약
    val tagSymbols: List<String>,     // 세탁 기호 리스트

    val video1Id: String,
    val video2Id: String,
    val video1Title: String,
    val video2Title: String
)
